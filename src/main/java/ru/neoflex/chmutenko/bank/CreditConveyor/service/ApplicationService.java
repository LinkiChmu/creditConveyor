package ru.neoflex.chmutenko.bank.CreditConveyor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.LoanOfferDTO;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import ru.neoflex.chmutenko.bank.CreditConveyor.service.util.CalculationUtil;


@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationService {

    private static long applicationId;
    private final CalculationUtil calculator;

    @Value("${loanOffer.baseRate}")
    private BigDecimal baseRate;
    @Value("${loanOffer.insurance}")
    private BigDecimal insuranceAmount;


    public List<LoanOfferDTO> getOffers(BigDecimal amount, int term) {
        applicationId++;
        log.info(String.format("Creating offer list with applicationId: %s", applicationId));
        List<LoanOfferDTO> offers = List.of(
                createOffer(applicationId, amount, term, false, false),
                createOffer(applicationId, amount, term, false, true),
                createOffer(applicationId, amount, term, true, false),
                createOffer(applicationId, amount, term, true, true)
        );
        log.info("Offer list created");

        return offers.stream().sorted(Comparator.comparing(LoanOfferDTO::getRate).reversed()).toList();
    }

    private LoanOfferDTO createOffer(long applicationId,
                                     BigDecimal requestedAmount,
                                     int term,
                                     boolean isInsuranceEnabled,
                                     boolean isSalaryClient) {

        LoanOfferDTO offer = LoanOfferDTO.builder()
                .applicationId(applicationId)
                .requestedAmount(requestedAmount)
                .term(term)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();

        BigDecimal totalAmount = calculator.calculateTotalAmount(requestedAmount, isInsuranceEnabled, insuranceAmount);
        offer.setTotalAmount(totalAmount);
        log.info(String.format("Setting totalAmount: %s", totalAmount));

        BigDecimal totalRate = calculateTotalRent(baseRate, isInsuranceEnabled, isSalaryClient);
        offer.setRate(totalRate);
        log.info(String.format("Setting totalRate: %s", totalRate.toString()));

        BigDecimal monthlyPayment = calculator.calculateMonthlyPayment(totalAmount, totalRate, term);
        offer.setMonthlyPayment(monthlyPayment);
        log.info(String.format("Setting monthlyPayment: %s", monthlyPayment));

        log.info("The offer created");
        return offer;
    }


    private BigDecimal calculateTotalRent(BigDecimal baseRate,
                                          boolean isInsuranceEnabled,
                                          boolean isSalaryClient) {

        // insurance reduces the rate by 3 points
        BigDecimal totalRate = (isInsuranceEnabled) ? baseRate.subtract(new BigDecimal(3)) : baseRate;
        // salary client reduces the rate by 1 points
        totalRate = (isSalaryClient) ? totalRate.subtract(new BigDecimal(1)) : totalRate;

        return totalRate;
    }
}