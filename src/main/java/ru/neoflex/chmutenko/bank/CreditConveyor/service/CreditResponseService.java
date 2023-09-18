package ru.neoflex.chmutenko.bank.CreditConveyor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.CreditDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.PaymentScheduleElement;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class CreditResponseService {

    private static final Logger logger = LoggerFactory.getLogger(CreditResponseService.class);

    public CreditDTO createCreditDTO(BigDecimal amount,
                                     int term,
                                     BigDecimal monthlyPayment,
                                     BigDecimal rate,
                                     BigDecimal psk,
                                     boolean isInsuranceEnabled,
                                     boolean isSalaryClient) {

        logger.info(("Starting createCreditDTO() with params amount %s, term %d, monthlyPayment %s, " +
                "rate %s, psk %s, isInsuranceEnabled %s, isSalaryClient %s")
                .formatted(amount.toString(),
                        term,
                        monthlyPayment.toString(),
                        rate.toString(),
                        psk.toString(),
                        isInsuranceEnabled,
                        isInsuranceEnabled));
        CreditDTO dto = new CreditDTO(amount, term, monthlyPayment, rate, psk, isInsuranceEnabled, isSalaryClient);

        dto.setPaymentSchedule(getSchedule(amount, term, monthlyPayment, rate));

        return dto;
    }

    private List<PaymentScheduleElement> getSchedule(BigDecimal amount,
                                                     int term,
                                                     BigDecimal monthlyPayment,
                                                     BigDecimal rate) {

        MathContext mathContext = new MathContext(6, RoundingMode.DOWN);
        List<PaymentScheduleElement> schedule = new ArrayList<>();
        LocalDate startDate = LocalDate.now();
        BigDecimal balanceDebt = amount;
        logger.info("BalanceDebt is set: %s".formatted(balanceDebt.toString()));

        for (int i = 1; i < term + 1; i++) {
            PaymentScheduleElement element = new PaymentScheduleElement();
            element.setNumber(i);

            LocalDate current = startDate.plusMonths(i);
            element.setDate(current);

            element.setTotalPayment(monthlyPayment);

            int daysMonth = current.lengthOfMonth();
            int daysYear = current.lengthOfYear();
            BigDecimal interestPayment = balanceDebt
                    .multiply(rate)
                    .multiply(BigDecimal.valueOf(daysMonth))
                    .divide(BigDecimal.valueOf(daysYear), mathContext)
                    .divide(BigDecimal.valueOf(100), mathContext);

            element.setInterestPayment(interestPayment);
            logger.info("Calculated interestPayment: %s".formatted(interestPayment.toString()));

            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment, mathContext);
            logger.info("Calculated debtPayment: %s".formatted(debtPayment.toString()));
            element.setDebtPayment(debtPayment);

            BigDecimal remainingDebt = balanceDebt.subtract(debtPayment, mathContext);
            element.setRemainingDebt(remainingDebt);

            balanceDebt = remainingDebt;
            logger.info("balanceDebt updated: %s".formatted(balanceDebt.toString()));

            schedule.add(element);
            logger.info("added %s".formatted(element));
        }
        return schedule;
    }
}