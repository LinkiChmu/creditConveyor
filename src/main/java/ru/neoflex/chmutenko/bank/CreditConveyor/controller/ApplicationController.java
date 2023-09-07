package ru.neoflex.chmutenko.bank.CreditConveyor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.LoanRequestDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.LoanOfferDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.ApplicationService;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/conveyor")
public class ApplicationController {

    private final ApplicationService applicationService;
    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/offers")
    public List<LoanOfferDTO> offerDTOS(@RequestBody @Validated LoanRequestDTO loanRequestDTO) {
        BigDecimal amount = loanRequestDTO.getAmount();
        logger.info(String.format("Extracted amount from LoanRequestDTO: %s", amount.toString()));
        int term = loanRequestDTO.getTerm();
        logger.info(String.format("Extracted term from LoanRequestDTO: %d", term));

        return applicationService.getOffers(amount, term);
    }
}
