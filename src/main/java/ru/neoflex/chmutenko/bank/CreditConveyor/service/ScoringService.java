package ru.neoflex.chmutenko.bank.CreditConveyor.service;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neoflex.chmutenko.bank.CreditConveyor.controller.CalculationController;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.EmploymentDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.ScoringDataDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.exceptions.LoanDeniedException;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentStatus;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.util.UtilCalculator;

import java.math.BigDecimal;

@Service
@NoArgsConstructor
public class ScoringService {

    private UtilCalculator calculator;
    private static final Logger logger = LoggerFactory.getLogger(CalculationController.class);

    @Autowired
    public ScoringService(UtilCalculator calculator) {
        this.calculator = calculator;
    }

    public void scoreData(ScoringDataDTO scoringDataDTO, EmploymentDTO employmentDTO) {
        if (isUnemployed(employmentDTO.getEmploymentStatus()) ||
                isRequestedAmountTooHigh(scoringDataDTO.getAmount(), employmentDTO.getSalary()) ||
                isAgeNotValid(calculator.countAge(scoringDataDTO.getBirthdate())) ||
                isWorkExperienceTotalNotValid(employmentDTO.getWorkExperienceTotal()) ||
                isWorkExperienceCurrentNotValid(employmentDTO.getWorkExperienceCurrent())
        ) {
            logger.warn("Credit scoring not passed. Throwing LoanDeniedException");
            throw new LoanDeniedException();
        }
        logger.info("Credit scoring is passed");
    }

    private boolean isUnemployed(EmploymentStatus status) {
        logger.info("Starting isUnemployed() with param EmploymentStatus %s".formatted(status));
        return status == EmploymentStatus.UNEMPLOYED;
    }

    private boolean isRequestedAmountTooHigh(BigDecimal requestedAmount, BigDecimal salary) {
        logger.info("Starting isRequestedAmountTooHigh() with params requestedAmount %s and salary %s".formatted(
                requestedAmount.toString(), salary.toString()));
        return requestedAmount.compareTo(salary.multiply(new BigDecimal(20))) == 1;
    }


    private boolean isAgeNotValid(int age) {
        logger.info("Starting isAgeNotValid() with param age %d".formatted(age));
        return age < 20 || age > 60;
    }

    private boolean isWorkExperienceTotalNotValid(int workExperienceTotal) {
        logger.info("Starting isWorkExperienceTotalNotValid() with param workExperienceTotal %d".formatted(workExperienceTotal));
        return workExperienceTotal < 12;
    }

    private boolean isWorkExperienceCurrentNotValid(int workExperienceCurrent) {
        logger.info("Starting isWorkExperienceCurrentNotValid() with param workExperienceCurrent %d".formatted(workExperienceCurrent));
        return workExperienceCurrent < 3;
    }
}
