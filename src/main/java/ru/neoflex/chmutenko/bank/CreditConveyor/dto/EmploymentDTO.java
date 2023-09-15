package ru.neoflex.chmutenko.bank.CreditConveyor.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.stereotype.Component;
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

    @NotNull
    @Min(value = 0)
    private BigDecimal salary;

    @EmploymentPositionSubset(anyOf = {EmploymentPosition.MIDDLE_MANAGER, EmploymentPosition.TOP_MANAGER, EmploymentPosition.WORKER})
    private EmploymentPosition position;

    @NotNull
    @Min(value = 0)
    private int workExperienceTotal;

    @NotNull
    @Min(value = 0)
    private int workExperienceCurrent ;


}
