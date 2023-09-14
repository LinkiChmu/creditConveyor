package ru.neoflex.chmutenko.bank.CreditConveyor.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentStatus;

import java.util.Arrays;

public class EmploymentStatusSubsetValidator implements ConstraintValidator<EmploymentStatusSubset, EmploymentStatus> {

    EmploymentStatus[] subset;

    @Override
    public void initialize(EmploymentStatusSubset constraintAnnotation) {
        this.subset = constraintAnnotation.anyOf();
    }

    @Override
    public boolean isValid(EmploymentStatus value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || Arrays.asList(subset).contains(value);
    }
}
