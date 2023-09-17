package ru.neoflex.chmutenko.bank.CreditConveyor.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.LoanRequestDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.LoanOfferDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.exceptions.DataNotValidException;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.ApplicationService;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/conveyor/offers")
public class ApplicationController {

    private final ApplicationService applicationService;
    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping()
    public List<LoanOfferDTO> offerDTOs(@RequestBody @Valid LoanRequestDTO loanRequestDTO,
                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append("\n");
            }
            throw new DataNotValidException(errorMsg.toString());
        }
        BigDecimal amount = loanRequestDTO.getAmount();
        logger.info(String.format("Extracted amount from LoanRequestDTO: %s", amount.toString()));
        int term = loanRequestDTO.getTerm();
        logger.info(String.format("Extracted term from LoanRequestDTO: %d", term));

        return applicationService.getOffers(amount, term);
    }
}
