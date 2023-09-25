package ru.neoflex.chmutenko.bank.CreditConveyor.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.MaritalStatus;

import java.util.Arrays;

public class MaritalStatusSubsetValidator implements ConstraintValidator<MaritalStatusSubset, MaritalStatus> {
    MaritalStatus[] subset;

    @Override
    public void initialize(MaritalStatusSubset constraintAnnotation) {
        this.subset = constraintAnnotation.anyOf();
    }

    @Override
    public boolean isValid(MaritalStatus value, ConstraintValidatorContext constraintValidatorContext) {
        return value == null || Arrays.asList(subset).contains(value);
    }
}
