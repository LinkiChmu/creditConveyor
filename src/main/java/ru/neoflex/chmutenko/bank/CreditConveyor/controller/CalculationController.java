package ru.neoflex.chmutenko.bank.CreditConveyor.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.CreditDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.EmploymentDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.PaymentScheduleElement;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.ScoringDataDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentStatus;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.ScoringService;

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
        System.out.println(scoringDataDTO);
        int age = countAge(scoringDataDTO.getBirthdate());
        if (
                isUnemployed(scoringDataDTO.getEmploymentDTO().getEmploymentStatus()) ||
                isRequestedAmountTooHigh(scoringDataDTO.getAmount(), scoringDataDTO.getEmploymentDTO().getSalary()) ||
                isAgeNotValid(age) ||
                isWorkExperienceTotalNotValid(scoringDataDTO.getEmploymentDTO().getWorkExperienceTotal()) ||
                isWorkExperienceCurrentNotValid(scoringDataDTO.getEmploymentDTO().getWorkExperienceCurrent())
        ) throw new IllegalArgumentException("Sorry! LOAN DENIED");




                return null;//scoringService.calculateCredit(scoringDataDTO);
    }

    private boolean isUnemployed(EmploymentStatus status) {
        return status == EmploymentStatus.UNEMPLOYED;
    }

    private boolean isRequestedAmountTooHigh(BigDecimal requestedAmount, BigDecimal salary) {
        return requestedAmount.compareTo(salary.multiply(new BigDecimal(20))) == 1;
    }

    private int countAge(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears();
    }

    private boolean isAgeNotValid(int age) {
        return age < 20 || age > 60;
    }

    private boolean isWorkExperienceTotalNotValid (int workExperienceTotal) {
        return workExperienceTotal < 12;
    }

    private boolean isWorkExperienceCurrentNotValid (int workExperienceCurrent) {
        return workExperienceCurrent < 3;
    }
}
