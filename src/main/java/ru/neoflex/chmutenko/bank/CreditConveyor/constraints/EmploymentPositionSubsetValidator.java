package ru.neoflex.chmutenko.bank.CreditConveyor.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentPosition;

import java.util.Arrays;

public class EmploymentPositionSubsetValidator implements ConstraintValidator<EmploymentPositionSubset, EmploymentPosition> {

    EmploymentPosition[] subset;

    @Override
    public void initialize(EmploymentPositionSubset constraintAnnotation) {
        this.subset = constraintAnnotation.anyOf();
    }

    @Override
    public boolean isValid(EmploymentPosition value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || Arrays.asList(subset).contains(value);
    }
}
