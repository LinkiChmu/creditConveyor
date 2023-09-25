package ru.neoflex.chmutenko.bank.CreditConveyor.service.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UtilCalculatorTest {

    private CalculationUtil calculator;
    private MathContext mathContext;

    @BeforeEach
    void setUp() {
        calculator = new CalculationUtil();
        mathContext = new MathContext(6, RoundingMode.HALF_UP);
    }

    @Test
    @DisplayName("Testing calculating of monthlyPayment")
    void calculateMonthlyPayment() {

        BigDecimal amount = new BigDecimal(1_000_000);
        int term = 2;
        BigDecimal rate = new BigDecimal(12);
        BigDecimal decimalMonthlyRate = new BigDecimal(0.010)
                .round(new MathContext(1, RoundingMode.HALF_UP));

        assertEquals(decimalMonthlyRate,
                calculator.monthlyRate(rate).divide(new BigDecimal(100)));

        BigDecimal monthlyPayment = new BigDecimal(507512);
        BigDecimal rate2 = new BigDecimal(12);

        assertEquals(monthlyPayment,
                calculator.calculateMonthlyPayment(amount, rate2, term));

    }

    @Test
    void monthlyRate() {
        BigDecimal rate1 = new BigDecimal(1.2);
        BigDecimal rate2 = new BigDecimal(6);
        BigDecimal rate3 = new BigDecimal(12);
        BigDecimal expected1 = new BigDecimal(0.100000).round(mathContext);
        BigDecimal expected2 = new BigDecimal(0.500000).round(mathContext);
        BigDecimal expected3 = new BigDecimal(1.000000).round(mathContext);

        assertEquals(expected1, calculator.monthlyRate(rate1));
        assertEquals(expected2, calculator.monthlyRate(rate2));
        assertEquals(expected3, calculator.monthlyRate(rate3));
    }

    @Test
    void countAge() {
        LocalDate current = LocalDate.now();
        LocalDate dateBeforeTwoYears = current.minusYears(2).minusMonths(1);
        LocalDate dateLessThanTwoYears = current.minusYears(2).plusMonths(1);
        int years = 2;
        int lessYears = 1;

        assertEquals(years, calculator.countAge(dateBeforeTwoYears));
        assertEquals(lessYears, calculator.countAge(dateLessThanTwoYears));
    }

    @Test
    void calculateTotalAmount() {
        BigDecimal amount = new BigDecimal(1000);
        BigDecimal insurance = new BigDecimal(5);
        boolean insuranceTrue = true;
        boolean insuranceFalse = false;
        BigDecimal amountInsTrue = new BigDecimal(1005);
        BigDecimal amountInsFalse = new BigDecimal(1000);

        assertEquals(amountInsTrue, calculator.calculateTotalAmount(amount, insuranceTrue, insurance));
        assertEquals(amountInsFalse, calculator.calculateTotalAmount(amount, insuranceFalse, insurance));
    }

    @Test
    void accumulatedRatio() {
        BigDecimal decimalMonthlyRate = new BigDecimal(0.01);
        int term = 2;
        BigDecimal accumulatedRatio = new BigDecimal(1.0201).round(mathContext);

        assertEquals(accumulatedRatio, calculator.accumulatedRatio(decimalMonthlyRate, term));
    }

    @Test
    void annuityRatio() {
        BigDecimal decimalMonthlyRate = new BigDecimal(0.01);
        BigDecimal accumulatedRatio = new BigDecimal(1.0201).round(mathContext);
        BigDecimal annuityRatio = new BigDecimal(0.507512).round(mathContext);

        assertEquals(annuityRatio,
                calculator.annuityRatio(decimalMonthlyRate, accumulatedRatio));
    }
}