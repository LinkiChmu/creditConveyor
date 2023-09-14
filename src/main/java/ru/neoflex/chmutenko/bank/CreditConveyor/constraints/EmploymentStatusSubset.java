package ru.neoflex.chmutenko.bank.CreditConveyor.constraints;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.neoflex.chmutenko.bank.CreditConveyor.models.EmploymentStatus;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = EmploymentStatusSubsetValidator.class)
@Documented
public @interface EmploymentStatusSubset {
    EmploymentStatus[] anyOf();
    String message() default "Employment status must be any of {anyOf}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
