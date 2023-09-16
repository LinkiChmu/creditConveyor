package ru.neoflex.chmutenko.bank.CreditConveyor.constraints;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class BooleanSubsetValidator implements ConstraintValidator<BooleanSubset, Boolean> {
    private boolean[] subset;

    @Override
    public void initialize(BooleanSubset constraintAnnotation) {
        this.subset = constraintAnnotation.anyOf();
    }

    @Override
    public boolean isValid(Boolean value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) return true;
        for (boolean b : subset) {
            if (b == value) return true;
        }
        return false;
    }
}
