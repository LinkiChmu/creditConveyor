package ru.neoflex.chmutenko.bank.CreditConveyor.service;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.neoflex.chmutenko.bank.CreditConveyor.controller.CalculationController;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.CreditDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.PaymentScheduleElement;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@NoArgsConstructor
public class CreditResponseService {

    private static final Logger logger = LoggerFactory.getLogger(CalculationController.class);


    public CreditDTO createCreditDTO(BigDecimal amount, int term, BigDecimal monthlyPayment, BigDecimal rate,
                                  BigDecimal psk, boolean isisInsuranceEnabled, boolean isSalaryClient) {
        CreditDTO dto = new CreditDTO(amount, term, monthlyPayment, rate, psk, isisInsuranceEnabled, isSalaryClient);
        dto.setPaymentSchedule(getSchedule(amount, term, monthlyPayment, rate));

        return dto;
    }

    private List<PaymentScheduleElement> getSchedule(BigDecimal amount, int term, BigDecimal monthlyPayment, BigDecimal rate) {
        MathContext mathContext = new MathContext(6, RoundingMode.DOWN);
        List<PaymentScheduleElement> schedule = new ArrayList<>();
        LocalDate startDate = LocalDate.now();
        BigDecimal balanceDebt = amount;
        for (int i = 1; i < term + 1; i++) {
            PaymentScheduleElement element = new PaymentScheduleElement();
            element.setNumber(i);
            LocalDate current = startDate.plusMonths(i);
            element.setDate(current);
            element.setTotalPayment(monthlyPayment);
            int daysMonth = current.lengthOfMonth();
            int daysYear = current.lengthOfYear();
            BigDecimal interestPayment = balanceDebt.multiply(rate)
                                                    .multiply(BigDecimal.valueOf(daysMonth))
                                                    .divide(BigDecimal.valueOf(daysYear), mathContext)
                                                    .divide(BigDecimal.valueOf(100), mathContext);
            element.setTotalPayment(monthlyPayment);
            element.setInterestPayment(interestPayment);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment, mathContext);
            element.setDebtPayment(debtPayment);
            element.setRemainingDebt(balanceDebt.subtract(debtPayment, mathContext));

            balanceDebt = balanceDebt.subtract(debtPayment);

            schedule.add(element);
            logger.info("Created and added to List<PaymentScheduleElement> an element: %s".formatted(element));

        }
        return schedule;
    }

}
