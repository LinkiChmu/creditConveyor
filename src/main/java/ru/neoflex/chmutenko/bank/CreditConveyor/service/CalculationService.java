package ru.neoflex.chmutenko.bank.CreditConveyor.service;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neoflex.chmutenko.bank.CreditConveyor.controller.CalculationController;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.EmploymentDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.dto.ScoringDataDTO;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentPosition;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentStatus;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.Gender;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.MaritalStatus;
import ru.neoflex.chmutenko.bank.CreditConveyor.service.util.UtilCalculator;

import java.math.BigDecimal;

@Service
@NoArgsConstructor
public class CalculationService {

    private UtilCalculator calculator;
    private static final Logger logger = LoggerFactory.getLogger(CalculationController.class);

    @Autowired
    public CalculationService(UtilCalculator calculator) {
        this.calculator = calculator;
    }

    public BigDecimal calculateTotalRate(BigDecimal baseRate, ScoringDataDTO scoringDataDTO, EmploymentDTO employmentDTO) {
        logger.info("Starting calculateTotalRate() with params baseRate %s, scoringDataDTO %s,  employmentDTO %s".formatted(
                baseRate.toString(), scoringDataDTO, employmentDTO));

        BigDecimal rate = setByEmploymentStatus(employmentDTO.getEmploymentStatus(), baseRate);
        rate = setByPosition(employmentDTO.getPosition(), rate);
        rate = setByMaritalStatus(scoringDataDTO.getMaritalStatus(), rate);
        rate = setByDependentsAmount(scoringDataDTO.getDependentAmount(), rate);
        rate = setByGenderAndAge(scoringDataDTO.getGender(), calculator.countAge(scoringDataDTO.getBirthdate()), rate);
        rate = setByInsuranceAndSalaryClient(scoringDataDTO.isInsuranceEnabled(), scoringDataDTO.isSalaryClient(), rate);

        logger.info("Method calculateTotalRate returned totalRate %s".formatted(rate.toString()));
        return rate;
    }

    public BigDecimal calculateTotalAmount(BigDecimal amount, boolean isInsuranceEnabled, BigDecimal insuranceAmount) {
        BigDecimal totalAmount = calculator.calculateTotalAmount(amount, isInsuranceEnabled, insuranceAmount);
        logger.info("method calculateTotalAmount() calculated totalAmount: %s".formatted(totalAmount.toString()));
        return totalAmount;
    }

    public BigDecimal calculateMonthlyPayment(BigDecimal amount, BigDecimal rate, int term) {
        return calculator.calculateMonthlyPayment(amount, rate, term);
    }

    public BigDecimal calculatePSK(BigDecimal rate, int term) {
        logger.info("Starting calculatePSK() with params rate %s,term %d"
                .formatted(rate.toString(), term));

        BigDecimal monthlyRate = calculator.monthlyRate(rate);
        BigDecimal psk = monthlyRate.multiply(BigDecimal.valueOf(12));

        logger.info("method calculatePSK() calculated PSK: %s".formatted(psk.toString()));
        return psk;
    }

    private BigDecimal setByEmploymentStatus(EmploymentStatus status, BigDecimal rate) {
        logger.info("Starting setByEmploymentStatus() with params EmploymentStatus %s, rate %s"
                .formatted(status, rate.toString()));
        if (status == EmploymentStatus.SELF_EMPLOYED) {
            rate = rate.add(BigDecimal.valueOf(1L));
        }
        if (status == EmploymentStatus.BUSINESS_OWNER) {
            rate = rate.add(BigDecimal.valueOf(3L));
        }
        logger.info("setByEmploymentStatus returned rate %s".formatted(rate.toString()));
        return rate;
    }

    private BigDecimal setByPosition(EmploymentPosition position, BigDecimal rate) {
        logger.info("Starting setByPosition() with params EmploymentPosition %s, rate %s"
                .formatted(position, rate.toString()));
        if (position == EmploymentPosition.TOP_MANAGER) {
            rate = rate.subtract(BigDecimal.valueOf(3L));
        }
        if (position == EmploymentPosition.MIDDLE_MANAGER) {
            rate = rate.subtract(BigDecimal.valueOf(2L));
        }
        logger.info("setByPosition() returned rate %s".formatted(rate.toString()));
        return rate;
    }

    private BigDecimal setByMaritalStatus(MaritalStatus status, BigDecimal rate) {
        logger.info("Starting setByMaritalStatus() with params status %s, rate %s"
                .formatted(status, rate.toString()));
        if (status == MaritalStatus.MARRIED) {
            rate = rate.subtract(BigDecimal.valueOf(1L));
        }
        if (status == MaritalStatus.DIVORCED) {
            rate = rate.add(BigDecimal.valueOf(1L));
        }
        logger.info("setByMaritalStatus() returned rate %s".formatted(rate.toString()));
        return rate;
    }

    private BigDecimal setByDependentsAmount(int dependentsAmount, BigDecimal rate) {
        logger.info("Starting setByDependentsAmount() with params dependentsAmount %d, rate %s"
                .formatted(dependentsAmount, rate.toString()));
        if (dependentsAmount > 1) {
            rate = rate.add(BigDecimal.valueOf(1L));
        }
        logger.info("setByDependentsAmount() returned rate %s".formatted(rate.toString()));
        return rate;
    }

    private BigDecimal setByGenderAndAge(Gender gender, int age, BigDecimal rate) {
        logger.info("Starting setByGenderAndAge() with params gender %s, age %d, rate %s"
                .formatted(gender, age, rate.toString()));
        if (gender == Gender.FEMALE && (age >= 35 && age <= 60)) {
            rate = rate.subtract(BigDecimal.valueOf(1L));
        }
        if (gender == Gender.MALE && (age >= 30 && age <= 55)) {
            rate = rate.subtract(BigDecimal.valueOf(1L));
        }
        if (gender == Gender.NON_BINARY) {
            rate = rate.add(BigDecimal.valueOf(1L));
        }
        logger.info("setByGenderAndAge() returned rate %s".formatted(rate.toString()));
        return rate;
    }

    private BigDecimal setByInsuranceAndSalaryClient(boolean isInsuranceEnabled, boolean isSalaryClient, BigDecimal rate) {
        logger.info("Starting setByInsuranceAndSalaryClient() with params isInsuranceEnabled %s, isSalaryClient %s, rate %s"
                .formatted(isInsuranceEnabled, isSalaryClient, rate.toString()));
        if (isInsuranceEnabled) {
            rate = rate.subtract(BigDecimal.valueOf(3L));
        }
        if (isSalaryClient) {
            rate = rate.subtract(BigDecimal.valueOf(1L));
        }
        logger.info("setByInsuranceAndSalaryClient() returned rate %s".formatted(rate.toString()));
        return rate;
    }
}
