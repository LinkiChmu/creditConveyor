package ru.neoflex.chmutenko.bank.CreditConveyor.service.util;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.ApplicationService;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

@Service
@NoArgsConstructor
@Slf4j
public class CalculationUtil {

    private final static MathContext mathContext = new MathContext(6, RoundingMode.HALF_UP);


    public BigDecimal calculateMonthlyPayment(BigDecimal amount,
                                              BigDecimal rate,
                                              int term) {
        log.info("Starting calculateMonthlyPayment() with params amount %s, rate %s, term %d"
                .formatted(amount.toString(), rate.toString(), term));

        BigDecimal decimalMonthlyRate = monthlyRate(rate).divide(new BigDecimal(100), mathContext);
        log.info("Calculated monthlyRate: %s".formatted(decimalMonthlyRate.toString()));

        BigDecimal accumulatedRatio = accumulatedRatio(decimalMonthlyRate, term);

        BigDecimal annuityRatio = annuityRatio(decimalMonthlyRate, accumulatedRatio);

        BigDecimal monthlyPayment = amount.multiply(annuityRatio, mathContext);
        log.info("Calculated monthlyPayment: %s".formatted(monthlyPayment.toString()));

        return monthlyPayment;
    }

    public BigDecimal monthlyRate(BigDecimal rate) {
        log.info("Starting annuityRatio() with param rate %s"
                .formatted(rate.toString()));

        BigDecimal monthlyRate = rate.divide(new BigDecimal(12), mathContext);
        log.info("Calculated monthlyRate: %s".formatted(monthlyRate.toString()));

        return monthlyRate;
    }


    public int countAge(LocalDate birthdate) {
        log.info("Starting countAge() with param LocalDate birthdate %s".formatted(birthdate.toString()));
        int age = Period.between(birthdate, LocalDate.now()).getYears();
        log.info("Counted age: %d".formatted(age));
        return age;
    }

    public BigDecimal calculateTotalAmount(BigDecimal amount,
                                           boolean isInsuranceEnabled,
                                           BigDecimal insuranceAmount) {
        return (isInsuranceEnabled) ? amount.add(insuranceAmount) : amount;
    }

    public BigDecimal accumulatedRatio(BigDecimal decimalMonthlyRate, int term) {

        BigDecimal accumulatedRatio = decimalMonthlyRate.add(new BigDecimal(1)).pow(term, mathContext);
        log.info("Calculated accumulatedRatio: %s".formatted(accumulatedRatio.toString()));
        return accumulatedRatio;
    }

    public BigDecimal annuityRatio(BigDecimal decimalMonthlyRate,
                                   BigDecimal accumulatedRatio) {

        BigDecimal annuityRatio = (decimalMonthlyRate.multiply(accumulatedRatio, mathContext))
                .divide(accumulatedRatio.subtract(new BigDecimal(1)), mathContext);
        log.info("Calculated annuityRatio: %s".formatted(annuityRatio.toString()));

        return annuityRatio;
    }

    public BigDecimal calculateInterestPayment(LocalDate currentDate,
                                               BigDecimal balanceDebt,
                                               BigDecimal rate){
        int daysMonth = currentDate.minusMonths(1).lengthOfMonth();
        int daysYear = currentDate.minusMonths(1).lengthOfYear();

        BigDecimal interestPayment = balanceDebt
                .multiply(rate)
                .multiply(BigDecimal.valueOf(daysMonth))
                .divide(BigDecimal.valueOf(daysYear), mathContext)
                .divide(BigDecimal.valueOf(100), mathContext);
        log.info("Calculated interestPayment: %s".formatted(interestPayment.toString()));

        return interestPayment;
    }

    public BigDecimal calculateDebtPayment(BigDecimal monthlyPayment,
                                           BigDecimal interestPayment) {
        BigDecimal debtPayment = monthlyPayment.subtract(interestPayment, mathContext);
        log.info("Calculated debtPayment: %s".formatted(debtPayment.toString()));

        return debtPayment;
    }

    public BigDecimal calculateRemainingDebt (BigDecimal balanceDebt,
                                           BigDecimal debtPayment) {
        BigDecimal remainingDebt = balanceDebt.subtract(debtPayment, mathContext);
        log.info("Calculated remainingDebt: %s".formatted(remainingDebt.toString()));

        return remainingDebt;
    }
}