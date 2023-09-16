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
import ru.neoflex.chmutenko.bank.CreditConveyor.service.CalculationService;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.ScoringService;
import ru.neoflex.chmutenko.bank.CreditConveyor.exceptions.DataNotValidException;

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
        scoringService.scoreData(scoringDataDTO, employmentDTO);

        BigDecimal totalRate = calculationService.calculateTotalRate(baseRate, scoringDataDTO, employmentDTO);
        logger.info("calculateTotalRate() returned totalRate: %s".formatted(totalRate.toString()));

        BigDecimal totalAmount = calculationService.calculateTotalAmount(scoringDataDTO.getAmount(), scoringDataDTO.isInsuranceEnabled(), insuranceAmount);

        BigDecimal monthlyPayment = calculationService.calculateMonthlyPayment(totalAmount, totalRate, scoringDataDTO.getTerm());

        BigDecimal psk = calculationService.calculatePSK(totalRate, scoringDataDTO.getTerm());


        return null;
    }


}
