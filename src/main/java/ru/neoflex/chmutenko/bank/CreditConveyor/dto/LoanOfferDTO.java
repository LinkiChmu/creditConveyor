package ru.neoflex.chmutenko.bank.CreditConveyor.dto;

import lombok.*;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanOfferDTO {

    private long applicationId;
    private BigDecimal requestedAmount;
    private BigDecimal totalAmount;
    private int term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private Boolean isInsuranceEnabled;
    private Boolean isSalaryClient;
}