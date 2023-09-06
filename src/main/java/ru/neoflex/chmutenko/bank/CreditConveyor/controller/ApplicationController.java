package ru.neoflex.chmutenko.bank.CreditConveyor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.LoanRequestDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.LoanOfferDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.ApplicationService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/conveyor")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/offers")
    private List<LoanOfferDTO> offerDTOS(@RequestBody LoanRequestDTO loanRequestDTO) {

        return applicationService.getOffers(loanRequestDTO);
    }
}
