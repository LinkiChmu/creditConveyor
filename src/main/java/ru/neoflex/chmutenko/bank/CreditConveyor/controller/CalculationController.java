package ru.neoflex.chmutenko.bank.CreditConveyor.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.ApplicationService;

import java.util.List;

@RestController
@RequestMapping("/conveyor/calculation")
public class CalculationController {

    private final ScoringService scoringService;
    private final CalculationService calculationService;
    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @PostMapping
    public List<PaymentScheduleElement> calculateCredit(@RequestBody @Valid ScoringDataDTO scoringDataDTO) {
        return null;
    }
}
