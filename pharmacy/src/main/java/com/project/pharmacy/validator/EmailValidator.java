package com.project.pharmacy.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailConstraint, String> {

    @Override
    public void initialize(EmailConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        if (string == null)
            return false;
        return string.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}
