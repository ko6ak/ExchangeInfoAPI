package com.example.exchangeinfoapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrencyValidator implements ConstraintValidator<CheckCurrency, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value.matches("[A-Z]{3}");
    }
}
