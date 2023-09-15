package ru.neoflex.chmutenko.bank.CreditConveyor.service;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.neoflex.chmutenko.bank.CreditConveyor.controller.CalculationController;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Service
@NoArgsConstructor
public class ScoringService {

    private static final Logger logger = LoggerFactory.getLogger(CalculationController.class);

    public boolean isUnemployed(EmploymentStatus status) {
        logger.info("Starting isUnemployed() with param EmploymentStatus %s".formatted(status));
        return status == EmploymentStatus.UNEMPLOYED;
    }

    public boolean isRequestedAmountTooHigh(BigDecimal requestedAmount, BigDecimal salary) {
        logger.info("Starting isRequestedAmountTooHigh() with params requestedAmount %s and salary %s".formatted(
                requestedAmount.toString(), salary.toString()));
        return requestedAmount.compareTo(salary.multiply(new BigDecimal(20))) == 1;
    }

    public int countAge(LocalDate birthdate) {
        logger.info("Starting countAge() with param LocalDate birthdate %s".formatted(birthdate.toString()));
        return Period.between(birthdate, LocalDate.now()).getYears();
    }

    public boolean isAgeNotValid(int age) {
        logger.info("Starting isAgeNotValid() with param age %d".formatted(age));
        return age < 20 || age > 60;
    }

    public boolean isWorkExperienceTotalNotValid(int workExperienceTotal) {
        logger.info("Starting isWorkExperienceTotalNotValid() with param workExperienceTotal %d".formatted(workExperienceTotal));
        return workExperienceTotal < 12;
    }

    public boolean isWorkExperienceCurrentNotValid(int workExperienceCurrent) {
        logger.info("Starting isWorkExperienceCurrentNotValid() with param workExperienceCurrent %d".formatted(workExperienceCurrent));
        return workExperienceCurrent < 3;
    }
}
