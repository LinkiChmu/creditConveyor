package ru.neoflex.chmutenko.bank.CreditConveyor.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentPosition;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmploymentPositionSubsetValidator.class)
@Documented
public @interface EmploymentPositionSubset {
    EmploymentPosition[] anyOf();
    String message() default "Position must be any of {anyOf}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
