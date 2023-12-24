package com.example.exchangeinfoapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrencyValidator.class)
public @interface CheckCurrency {
    String value() default "";
    String message() default "Incorrect currency name.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
