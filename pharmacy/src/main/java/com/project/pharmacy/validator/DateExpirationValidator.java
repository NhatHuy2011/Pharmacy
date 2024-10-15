package com.project.pharmacy.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
public class DateExpirationValidator implements ConstraintValidator<DateExpirationConstraint, LocalDate> {
    @Override
    public void initialize(DateExpirationConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate dateExpiration, ConstraintValidatorContext constraintValidatorContext) {
        return !dateExpiration.isBefore(LocalDate.now());
    }
}
