package com.project.pharmacy.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateExpirationValidator.class)
public @interface DateExpirationConstraint {
    String message() default "Ngày hết hạn phải lớn hơn ngày tạo";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
