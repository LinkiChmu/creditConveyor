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

    // formula for calculating annuityRatio = ( monthlyRate * (1 + monthlyRate)^term ) / (1 + monthlyRate)^term - 1
    public BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, int term) {
        logger.info("Starting calculateMonthlyPayment() with params amount %s, rate %s, term %d"
                .formatted(amount.toString(), rate.toString(), term));

        MathContext mathContext = new MathContext(6, RoundingMode.HALF_UP);

        BigDecimal monthlyRate = rate.divide(new BigDecimal(12), mathContext).divide(new BigDecimal(100), mathContext);
        logger.info("Calculated monthlyRate: %s".formatted(monthlyRate.toString()));

        BigDecimal accumulatedRatio = monthlyRate.add(new BigDecimal(1)).pow(term, mathContext);
        logger.info("Calculated accumulatedRatio: %s".formatted(accumulatedRatio.toString()));

        BigDecimal annuityRatio = (monthlyRate.multiply(accumulatedRatio, mathContext))
                .divide(accumulatedRatio.subtract(new BigDecimal(1)), mathContext);
        logger.info("Calculated annuityRatio: %s".formatted(annuityRatio.toString()));

        BigDecimal monthlyPayment = amount.multiply(annuityRatio, mathContext);
        logger.info("Calculated monthlyPayment: %s".formatted(monthlyPayment.toString()));

        return monthlyPayment;
    }

    public BigDecimal monthlyRate(BigDecimal rate) {
        logger.info("Starting annuityRatio() with param rate %s"
                .formatted(rate.toString()));

        MathContext mathContext = new MathContext(6, RoundingMode.HALF_UP);

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

    public BigDecimal calculateTotalAmount(BigDecimal amount, boolean isInsuranceEnabled, BigDecimal insuranceAmount) {
        return (isInsuranceEnabled) ? amount.add(insuranceAmount) : amount;
    }

}
