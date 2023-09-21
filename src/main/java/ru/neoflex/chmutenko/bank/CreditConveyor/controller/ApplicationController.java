package ru.neoflex.chmutenko.bank.CreditConveyor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.LoanRequestDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.LoanOfferDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.exceptions.DataNotValidException;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.ApplicationService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/conveyor/offers")
@Slf4j
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping()
    public ResponseEntity<List<LoanOfferDTO>> offerDTOs(@RequestBody @Valid LoanRequestDTO loanRequestDTO,
                                                        BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append("; ");
            }
            log.error("Exception from method offerDTOs: %s".formatted(errorMsg.toString()));
            throw new DataNotValidException(errorMsg.toString());
        }
        BigDecimal amount = loanRequestDTO.getAmount();
        log.info(String.format("Extracted amount from LoanRequestDTO: %s", amount.toString()));
        int term = loanRequestDTO.getTerm();
        log.info(String.format("Extracted term from LoanRequestDTO: %d", term));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(applicationService.getOffers(amount, term));
    }
}
