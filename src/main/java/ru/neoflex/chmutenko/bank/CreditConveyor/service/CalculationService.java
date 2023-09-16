package ru.neoflex.chmutenko.bank.CreditConveyor.service;

import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.neoflex.chmutenko.bank.CreditConveyor.controller.CalculationController;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentPosition;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentStatus;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.Gender;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.MaritalStatus;

import java.math.BigDecimal;

@Service
@NoArgsConstructor
public class CalculationService {

    private static final Logger logger = LoggerFactory.getLogger(CalculationController.class);

    public BigDecimal calculateRate(BigDecimal baseRate, EmploymentStatus employmentStatus,
                                    EmploymentPosition position,
                                    MaritalStatus maritalStatus,
                                    int dependentAmount, Gender gender, int age,
                                    boolean isInsuranceEnabled, boolean isSalaryClient) {
        logger.info(String.format(
                "Starting calculateRate() with params baseRate %s, employmentStatus %s, employmentPosition %s, maritalStatus %s, dependentAmount %d, gender %s, age %d",
                baseRate.toString(), employmentStatus, position, maritalStatus, dependentAmount, gender, age));
        return setByInsuranceAndSalaryClient(isInsuranceEnabled, isSalaryClient,
                setByGenderAndAge(gender, age,
                        setByDependentsAmount(dependentAmount,
                                setByMaritalStatus(maritalStatus, setByPosition(position,
                                        setByEmploymentStatus(employmentStatus, baseRate))))));
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
