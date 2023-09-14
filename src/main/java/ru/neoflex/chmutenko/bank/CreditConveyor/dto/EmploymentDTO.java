package ru.neoflex.chmutenko.bank.CreditConveyor.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import ru.neoflex.chmutenko.bank.CreditConveyor.constraints.EmploymentPositionSubset;
import ru.neoflex.chmutenko.bank.CreditConveyor.constraints.EmploymentStatusSubset;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentPosition;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentStatus;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class EmploymentDTO {

    @NotNull
    @EmploymentStatusSubset(anyOf = {EmploymentStatus.EMPLOYED, EmploymentStatus.UNEMPLOYED, EmploymentStatus.SELF_EMPLOYED, EmploymentStatus.BUSINESS_OWNER})
    private EmploymentStatus employmentStatus;

    @Pattern(regexp = "^[\\d]{10}$")
    private String employerINN;

    private BigDecimal salary;

    @EmploymentPositionSubset(anyOf = {EmploymentPosition.MIDDLE_MANAGER, EmploymentPosition.TOP_MANAGER, EmploymentPosition.WORKER})
    private EmploymentPosition position;

    private int workExperienceTotal;

    private int workExperienceCurrent ;

}
