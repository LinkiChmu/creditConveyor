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
    private static long applicationId;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    public ApplicationService() {
    }

    public List<LoanOfferDTO> getOffers(BigDecimal amount, int term) {
        applicationId++;
        List<LoanOfferDTO> offers = new ArrayList<>();
        logger.info("Creating offer list");

        offers.add(createOffer(applicationId, amount, term, false, false));
        offers.add(createOffer(applicationId, amount, term, false, true));
        offers.add(createOffer(applicationId, amount, term, true, false));
        offers.add(createOffer(applicationId, amount, term, true, true));

        return offers.stream().sorted(Comparator.comparing(LoanOfferDTO::getRate).reversed()).toList();
    }

    private LoanOfferDTO createOffer(long applicationId, BigDecimal amount, int term, boolean isInsuranceEnabled, boolean isSalaryClient) {
        LoanOfferDTO offer = new LoanOfferDTO(amount, term);
        offer.setApplicationId(applicationId);
        offer.setRequestedAmount(amount);
        offer.setTerm(term);

        offer.setIsInsuranceEnabled(isInsuranceEnabled);
        logger.info(String.format("Setting isInsuranceEnabled: %b", isInsuranceEnabled));
        offer.setIsSalaryClient(isSalaryClient);
        logger.info(String.format("Setting isSalaryClient: %b", isSalaryClient));

        BigDecimal totalAmount = calculateTotalAmount(amount, isInsuranceEnabled, insuranceAmount);
        offer.setTotalAmount(totalAmount);
        logger.info(String.format("Setting totalAmount: %s", totalAmount));

        BigDecimal totalRate = calculateTotalRent(baseRate, isInsuranceEnabled, isSalaryClient);
        offer.setRate(totalRate);
        logger.info(String.format("Setting totalRate: %s", totalRate.toString()));

        BigDecimal monthlyPayment = calculateMonthlyPayment(totalAmount, totalRate, term);
        offer.setMonthlyPayment(monthlyPayment);
        logger.info(String.format("Setting monthlyPayment: %s", monthlyPayment));

        logger.info("The offer created");
        return offer;
    }

    // insurance increases the total amount by 100,000
    private BigDecimal calculateTotalAmount(BigDecimal amount, boolean isInsuranceEnabled, BigDecimal insuranceAmount) {
        return (isInsuranceEnabled) ? amount.add(insuranceAmount) : amount;
    }

    private BigDecimal calculateTotalRent(BigDecimal baseRate, boolean isInsuranceEnabled, boolean isSalaryClient) {
        // insurance reduces the rate by 3 points
        BigDecimal totalRate = (isInsuranceEnabled) ? baseRate.subtract(new BigDecimal(3)) : baseRate;

        // salary client reduces the rate by 1 points
        totalRate = (isSalaryClient) ? totalRate.subtract(new BigDecimal(1)) : totalRate;

        return totalRate;
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
