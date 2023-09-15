package ru.neoflex.chmutenko.bank.CreditConveyor.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.CreditDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.EmploymentDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.PaymentScheduleElement;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.ScoringDataDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentStatus;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.ScoringService;
import ru.neoflex.chmutenko.bank.CreditConveyor.util.LoanDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@RestController
@RequestMapping("/conveyor/calculation")
public class CalculationController {

    private final ScoringService scoringService;
    //private final CalculationService calculationService;
    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @Autowired
    public CalculationController(ScoringService scoringService) {
        this.scoringService = scoringService;
        //this.calculationService = calculationService;
    }

    @PostMapping
    public CreditDTO calculateCredit (@RequestBody @Valid ScoringDataDTO scoringDataDTO) {
        logger.info("Starting calculateCredit() with param scoringDataDTO %s".formatted(scoringDataDTO));
        int age = countAge(scoringDataDTO.getBirthdate());
        logger.info("Counted age: %d".formatted(age));
        if (
                isUnemployed(scoringDataDTO.getEmploymentDTO().getEmploymentStatus()) ||
                isRequestedAmountTooHigh(scoringDataDTO.getAmount(), scoringDataDTO.getEmploymentDTO().getSalary()) ||
                isAgeNotValid(age) ||
                isWorkExperienceTotalNotValid(scoringDataDTO.getEmploymentDTO().getWorkExperienceTotal()) ||
                isWorkExperienceCurrentNotValid(scoringDataDTO.getEmploymentDTO().getWorkExperienceCurrent())
        ) {
            logger.warn("Loan scoring not passed. Throwing LoanDeniedException");
            throw new LoanDeniedException();}

        return null;//scoringService.calculateCredit(scoringDataDTO);
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

    private int countAge(LocalDate birthdate) {
        logger.info("Starting countAge() with param LocalDate birthdate %s".formatted(birthdate.toString()));
        return Period.between(birthdate, LocalDate.now()).getYears();
    }

    private boolean isAgeNotValid(int age) {
        logger.info("Starting isAgeNotValid() with param age %d".formatted(age));
        return age < 20 || age > 60;
    }

    private boolean isWorkExperienceTotalNotValid (int workExperienceTotal) {
        logger.info("Starting isWorkExperienceTotalNotValid() with param workExperienceTotal %d".formatted(workExperienceTotal));
        return workExperienceTotal < 12;
    }

    private boolean isWorkExperienceCurrentNotValid (int workExperienceCurrent) {
        logger.info("Starting isWorkExperienceCurrentNotValid() with param workExperienceCurrent %d".formatted(workExperienceCurrent));
        return workExperienceCurrent < 3;
    }


}
