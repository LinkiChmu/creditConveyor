package ru.neoflex.chmutenko.bank.CreditConveyor.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.CreditDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.EmploymentDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.ScoringDataDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentStatus;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.ScoringService;
import ru.neoflex.chmutenko.bank.CreditConveyor.util.LoanDeniedException;
import ru.neoflex.chmutenko.bank.CreditConveyor.util.DataNotValidException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@RestController
@RequestMapping("/conveyor/calculation")
public class CalculationController {

    private final ScoringService scoringService;
    //private final CalculationService calculationService;
    private static final Logger logger = LoggerFactory.getLogger(CalculationController.class);

    @Autowired
    public CalculationController(ScoringService scoringService) {
        this.scoringService = scoringService;
        //this.calculationService = calculationService;
    }

    @PostMapping
    public CreditDTO calculateCredit (@RequestBody @Valid ScoringDataDTO scoringDataDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error:errors) {
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append("\n");
            }
            throw new DataNotValidException(errorMsg.toString());
        }
        logger.info("Starting calculateCredit() with param scoringDataDTO %s".formatted(scoringDataDTO));
        int age = scoringService.countAge(scoringDataDTO.getBirthdate());
        logger.info("Counted age: %d".formatted(age));
        if (
                scoringService.isUnemployed(scoringDataDTO.getEmploymentDTO().getEmploymentStatus()) ||
                        scoringService.isRequestedAmountTooHigh(scoringDataDTO.getAmount(), scoringDataDTO.getEmploymentDTO().getSalary()) ||
                        scoringService.isAgeNotValid(age) ||
                        scoringService.isWorkExperienceTotalNotValid(scoringDataDTO.getEmploymentDTO().getWorkExperienceTotal()) ||
                        scoringService.isWorkExperienceCurrentNotValid(scoringDataDTO.getEmploymentDTO().getWorkExperienceCurrent())
        ) {
            logger.warn("Loan scoring not passed. Throwing LoanDeniedException");
            throw new LoanDeniedException();}

        return null;
    }




}
