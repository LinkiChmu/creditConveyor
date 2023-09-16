package ru.neoflex.chmutenko.bank.CreditConveyor.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.CreditDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.EmploymentDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.ScoringDataDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentStatus;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.CalculationService;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.ScoringService;
import ru.neoflex.chmutenko.bank.CreditConveyor.util.LoanDeniedException;
import ru.neoflex.chmutenko.bank.CreditConveyor.util.DataNotValidException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/conveyor/calculation")
public class CalculationController {

    private final ScoringService scoringService;
    private final CalculationService calculationService;
    private static final Logger logger = LoggerFactory.getLogger(CalculationController.class);
    @Value("${loanOffer.baseRate}")
    private BigDecimal baseRate;
    @Value("${loanOffer.insurance}")
    private BigDecimal insuranceAmount;

    @Autowired
    public CalculationController(ScoringService scoringService, CalculationService calculationService) {
        this.scoringService = scoringService;
        this.calculationService = calculationService;
    }

    @PostMapping
    public CreditDTO calculateCredit(@RequestBody @Valid ScoringDataDTO scoringDataDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append("\n");
            }
            throw new DataNotValidException(errorMsg.toString());
        }
        logger.info("Starting calculateCredit() with param scoringDataDTO %s".formatted(scoringDataDTO));

        EmploymentDTO employmentDTO = scoringDataDTO.getEmploymentDTO();
        logger.info("EmploymentDTO extracted from ScoringDataDTO: %s".formatted(employmentDTO));

        int age = scoringService.countAge(scoringDataDTO.getBirthdate());
        logger.info("Counted age: %d".formatted(age));

        if (scoringService.isUnemployed(employmentDTO.getEmploymentStatus()) ||
                scoringService.isRequestedAmountTooHigh(scoringDataDTO.getAmount(), employmentDTO.getSalary()) ||
                scoringService.isAgeNotValid(age) ||
                scoringService.isWorkExperienceTotalNotValid(employmentDTO.getWorkExperienceTotal()) ||
                scoringService.isWorkExperienceCurrentNotValid(employmentDTO.getWorkExperienceCurrent())
        ) {
            logger.warn("Credit scoring not passed. Throwing LoanDeniedException");
            throw new LoanDeniedException();
        }
        logger.info("Credit scoring is passed");

        BigDecimal totalRate = calculationService.calculateRate(baseRate, employmentDTO.getEmploymentStatus(),
                employmentDTO.getPosition(), scoringDataDTO.getMaritalStatus(), scoringDataDTO.getDependentAmount(),
                scoringDataDTO.getGender(), age, scoringDataDTO.isInsuranceEnabled(), scoringDataDTO.isSalaryClient());
        logger.info("calculateRate() calculated totalRate %s".formatted(totalRate.toString()));

        return null;
    }


}
