package ru.neoflex.chmutenko.bank.CreditConveyor.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
public class CreditDTO {
    private BigDecimal amount;
    private int term;
    private BigDecimal monthlyPayment;
    private BigDecimal rate;
    private BigDecimal psk;
    private boolean isInsuranceEnabled;
    private boolean isSalaryClient;
    private List<PaymentScheduleElement> paymentSchedule;

    public CreditDTO(BigDecimal amount, int term, BigDecimal monthlyPayment, BigDecimal rate, BigDecimal psk, boolean isInsuranceEnabled, boolean isSalaryClient) {
        this.amount = amount;
        this.term = term;
        this.monthlyPayment = monthlyPayment;
        this.rate = rate;
        this.psk = psk;
        this.isInsuranceEnabled = isInsuranceEnabled;
        this.isSalaryClient = isSalaryClient;
    }
}
