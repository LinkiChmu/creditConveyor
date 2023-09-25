package ru.neoflex.chmutenko.bank.CreditConveyor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.EmploymentDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.ScoringDataDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.exceptions.LoanDeniedException;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentStatus;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.util.CalculationUtil;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoringService {

    private final CalculationUtil calculator;

    public void scoreData(ScoringDataDTO scoringDataDTO,
                          EmploymentDTO employmentDTO) {

        if (isUnemployed(employmentDTO.getEmploymentStatus()) ||
                isRequestedAmountTooHigh(scoringDataDTO.getAmount(), employmentDTO.getSalary()) ||
                isAgeNotValid(calculator.countAge(scoringDataDTO.getBirthdate())) ||
                isWorkExperienceTotalNotValid(employmentDTO.getWorkExperienceTotal()) ||
                isWorkExperienceCurrentNotValid(employmentDTO.getWorkExperienceCurrent())
        ) {
            log.warn("Credit scoring not passed. Throwing LoanDeniedException from method scoreData");
            throw new LoanDeniedException();
        }
        log.info("Credit scoring is passed");
    }

    private boolean isUnemployed(EmploymentStatus status) {
        log.info("Starting isUnemployed() with param EmploymentStatus %s".formatted(status));
        return status == EmploymentStatus.UNEMPLOYED;
    }

    private boolean isRequestedAmountTooHigh(BigDecimal requestedAmount, BigDecimal salary) {
        log.info("Starting isRequestedAmountTooHigh() with params requestedAmount %s and salary %s"
                .formatted(requestedAmount.toString(), salary.toString()));
        return requestedAmount.compareTo(salary.multiply(new BigDecimal(20))) == 1;
    }

    private boolean isAgeNotValid(int age) {
        log.info("Starting isAgeNotValid() with param age %d".formatted(age));
        return age < 20 || age > 60;
    }

    private boolean isWorkExperienceTotalNotValid(int workExperienceTotal) {
        log.info("Starting isWorkExperienceTotalNotValid() with param workExperienceTotal %d"
                .formatted(workExperienceTotal));
        return workExperienceTotal < 12;
    }

    private boolean isWorkExperienceCurrentNotValid(int workExperienceCurrent) {
        log.info("Starting isWorkExperienceCurrentNotValid() with param workExperienceCurrent %d"
                .formatted(workExperienceCurrent));
        return workExperienceCurrent < 3;
    }
}