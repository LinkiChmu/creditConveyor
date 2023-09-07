package ru.neoflex.chmutenko.bank.CreditConveyor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.LoanOfferDTO;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


@Service
public class ApplicationService {

    @Value("${loanOffer.baseRate}")
    private BigDecimal baseRate;
    @Value("${loanOffer.insurance}")
    private BigDecimal insuranceAmount;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    public ApplicationService() {
    }

    public List<LoanOfferDTO> getOffers(BigDecimal amount, int term) {
        List<LoanOfferDTO> offers = new ArrayList<>();
        logger.info("Creating offer list");

        offers.add(createOffer(amount, term, false, false));
        offers.add(createOffer(amount, term, false, true));
        offers.add(createOffer(amount, term, true, false));
        offers.add(createOffer(amount, term, true, true));

        return offers.stream().sorted(Comparator.comparing(LoanOfferDTO::getRate).reversed()).toList();
    }

    private LoanOfferDTO createOffer(BigDecimal amount, int term, boolean isInsuranceEnabled, boolean isSalaryClient) {
        LoanOfferDTO offer = new LoanOfferDTO(amount, term);

        offer.setRequestedAmount(amount);
        offer.setTerm(term);

        offer.setIsInsuranceEnabled(isInsuranceEnabled);
        logger.info(String.format("Setting isInsuranceEnabled: %b", isInsuranceEnabled));
        offer.setIsSalaryClient(isSalaryClient);
        logger.info(String.format("Setting isSalaryClient: %b", isSalaryClient));

        // insurance increases the total amount by 100,000 and reduces the rate by 3 points
        BigDecimal totalAmount = (isInsuranceEnabled) ? amount.add(insuranceAmount) : amount;
        offer.setTotalAmount(totalAmount);
        logger.info(String.format("Setting totalAmount: %s", totalAmount.toString()));

        BigDecimal totalRate = (isInsuranceEnabled) ? baseRate.subtract(new BigDecimal(3)) : baseRate;

        // salary client reduces the rate by 1 points
        totalRate = (isSalaryClient) ? totalRate.subtract(new BigDecimal(1)) : totalRate;
        offer.setRate(totalRate);
        logger.info(String.format("Setting totalRate: %s", totalRate.toString()));

        BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, totalRate, term);
        offer.setMonthlyPayment(monthlyPayment);
        logger.info(String.format("Setting monthlyPayment: %s", monthlyPayment));
        logger.info("The offer created");

        return offer;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, int term) {
        MathContext mathContext = new MathContext(6, RoundingMode.HALF_UP);
        BigDecimal monthlyRate = rate.divide(new BigDecimal(term), mathContext).divide(new BigDecimal(100), mathContext);
        logger.info(String.format("Calculated monthlyRate: %s", monthlyRate));

        BigDecimal accumulatedRatio = monthlyRate.add(new BigDecimal(1)).pow(term, mathContext);
        logger.info(String.format("Calculated accumulatedRatio: %s", accumulatedRatio));

        BigDecimal annuityRatio = (monthlyRate.multiply(accumulatedRatio, mathContext))
                .divide(accumulatedRatio.subtract(new BigDecimal(1)), mathContext);
        logger.info(String.format("Calculated annuityRatio: %s", annuityRatio));

        return amount.multiply(annuityRatio, mathContext);
    }
}
