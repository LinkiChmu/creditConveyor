package ru.neoflex.chmutenko.bank.CreditConveyor.service.util;

import lombok.NoArgsConstructor;
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
public class UtilCalculator {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);
    private final MathContext mathContext = new MathContext(6, RoundingMode.HALF_UP);


    public BigDecimal calculateMonthlyPayment(BigDecimal amount,
                                              BigDecimal rate,
                                              int term) {
        logger.info("Starting calculateMonthlyPayment() with params amount %s, rate %s, term %d"
                .formatted(amount.toString(), rate.toString(), term));

        BigDecimal decimalMonthlyRate = monthlyRate(rate).divide(new BigDecimal(100), mathContext);
        logger.info("Calculated monthlyRate: %s".formatted(decimalMonthlyRate.toString()));

        BigDecimal accumulatedRatio = accumulatedRatio(decimalMonthlyRate, term);

        BigDecimal annuityRatio = annuityRatio(decimalMonthlyRate, accumulatedRatio);

        BigDecimal monthlyPayment = amount.multiply(annuityRatio, mathContext);
        logger.info("Calculated monthlyPayment: %s".formatted(monthlyPayment.toString()));

        return monthlyPayment;
    }

    public BigDecimal monthlyRate(BigDecimal rate) {
        logger.info("Starting annuityRatio() with param rate %s"
                .formatted(rate.toString()));

        BigDecimal monthlyRate = rate.divide(new BigDecimal(12), mathContext);
        logger.info("Calculated monthlyRate: %s".formatted(monthlyRate.toString()));

        return monthlyRate;
    }


    public int countAge(LocalDate birthdate) {
        logger.info("Starting countAge() with param LocalDate birthdate %s".formatted(birthdate.toString()));
        int age = Period.between(birthdate, LocalDate.now()).getYears();
        logger.info("Counted age: %d".formatted(age));
        return age;
    }

    public BigDecimal calculateTotalAmount(BigDecimal amount,
                                           boolean isInsuranceEnabled,
                                           BigDecimal insuranceAmount) {
        return (isInsuranceEnabled) ? amount.add(insuranceAmount) : amount;
    }

    public BigDecimal accumulatedRatio(BigDecimal decimalMonthlyRate, int term) {

        BigDecimal accumulatedRatio = decimalMonthlyRate.add(new BigDecimal(1)).pow(term, mathContext);
        logger.info("Calculated accumulatedRatio: %s".formatted(accumulatedRatio.toString()));
        return accumulatedRatio;
    }

    // formula annuityRatio = ( decimalMonthlyRate * accumulatedRatio ) / (accumulatedRatio - 1)
    public BigDecimal annuityRatio(BigDecimal decimalMonthlyRate,
                                   BigDecimal accumulatedRatio) {

        BigDecimal annuityRatio = (decimalMonthlyRate.multiply(accumulatedRatio, mathContext))
                .divide(accumulatedRatio.subtract(new BigDecimal(1)), mathContext);
        logger.info("Calculated annuityRatio: %s".formatted(annuityRatio.toString()));

        return annuityRatio;
    }
}