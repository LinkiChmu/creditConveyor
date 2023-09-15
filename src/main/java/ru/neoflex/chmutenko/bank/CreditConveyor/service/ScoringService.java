package ru.neoflex.chmutenko.bank.CreditConveyor.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.CreditDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.PaymentScheduleElement;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.ScoringDataDTO;

import java.math.BigDecimal;
import java.util.List;

@Service
@NoArgsConstructor
@Slf4j
public class ScoringService {

    @Value("${loanOffer.baseRate}")
    private BigDecimal baseRate;
    @Value("${loanOffer.insurance}")
    private BigDecimal insuranceAmount;
    private static long applicationId;

    public CreditDTO calculateCredit(ScoringDataDTO scoringDataDTO) {
        CreditDTO creditDTO = new CreditDTO();
        return creditDTO;
    }

}
