package ru.neoflex.chmutenko.bank.CreditConveyor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.LoanOfferDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.LoanRequestDTO;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ApplicationService {

    @Value("${loanOffer.baseRate}")
    private BigDecimal baseRate;
    @Value("${loanOffer.insurance}")
    private BigDecimal insuranceAmount;


    public ApplicationService() {
    }

    public List<LoanOfferDTO> getOffers(LoanRequestDTO loanRequestDTO) {
        List<LoanOfferDTO> offers = new ArrayList<>();

        BigDecimal amount = loanRequestDTO.getAmount();
        int term = loanRequestDTO.getTerm();

        offers.add(createOffer(amount, term, false, false));
        offers.add(createOffer(amount, term, false, true));
        offers.add(createOffer(amount, term, true, false));
        offers.add(createOffer(amount, term, true, true));

        return offers.stream().sorted(Comparator.comparing(LoanOfferDTO::hashCode)).toList();
    }

    private LoanOfferDTO createOffer(BigDecimal amount, int term, boolean isInsuranceEnabled, boolean isSalaryClient) {
        LoanOfferDTO offer = new LoanOfferDTO(amount, term);

        offer.setRequestedAmount(amount);
        offer.setTerm(term);
        offer.setIsInsuranceEnabled(isInsuranceEnabled);
        offer.setIsSalaryClient(isSalaryClient);

        // insurance increases the total amount by 100,000 and reduces the rate by 3 points
        BigDecimal totalAmount = (isInsuranceEnabled) ? amount.add(insuranceAmount) : amount;
        offer.setTotalAmount(totalAmount);
        BigDecimal totalRate = (isInsuranceEnabled) ? baseRate.subtract(new BigDecimal(3)) : baseRate;

        // salary client reduces the rate by 1 points
        totalRate = (isSalaryClient) ? totalRate.subtract(new BigDecimal(1)) : totalRate;
        offer.setRate(totalRate);

        offer.setMonthlyPayment(calculateMonthlyPayment(totalAmount, totalRate, term));

        return offer;
    }

    private BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, int term) {
        MathContext mathContext = new MathContext(3, RoundingMode.HALF_UP);
        BigDecimal monthlyRate = rate.divide(new BigDecimal(term), mathContext);
        BigDecimal accumulatedRatio = monthlyRate.add(new BigDecimal(1)).pow(term, mathContext);
        BigDecimal annuityRatio = (monthlyRate.multiply(accumulatedRatio, mathContext))
                .divide(accumulatedRatio.subtract(new BigDecimal(1)), mathContext);

        return amount.multiply(annuityRatio, mathContext);
    }
}
