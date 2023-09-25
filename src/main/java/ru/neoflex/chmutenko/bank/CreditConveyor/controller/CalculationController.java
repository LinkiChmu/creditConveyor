package ru.neoflex.chmutenko.bank.CreditConveyor.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.CreditDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.EmploymentDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.ScoringDataDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.CalculationService;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.ScoringService;
import ru.neoflex.chmutenko.bank.CreditConveyor.exceptions.DataNotValidException;

import java.util.List;

@RestController
@RequestMapping("/conveyor/calculation")
@Slf4j
@RequiredArgsConstructor
public class CalculationController {

    private final ScoringService scoringService;
    private final CalculationService calculationService;


    @PostMapping
    public ResponseEntity<CreditDTO> calculateCredit(@RequestBody @Valid ScoringDataDTO scoringDataDTO,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append("\n");
            }
            log.error("Exception from method calculateCredit: %s".formatted(errorMsg.toString()));
            throw new DataNotValidException(errorMsg.toString());
        }
        log.info("Starting calculateCredit() with param scoringDataDTO %s".formatted(scoringDataDTO));

        EmploymentDTO employmentDTO = scoringDataDTO.getEmploymentDTO();
        log.info("EmploymentDTO extracted from ScoringDataDTO: %s".formatted(employmentDTO));
        scoringService.scoreData(scoringDataDTO, employmentDTO);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(calculationService.getCreditDTO(scoringDataDTO, employmentDTO));
    }
}