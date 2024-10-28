package com.project.pharmacy.dto.request;

import com.project.pharmacy.validator.EmailConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserForgotPassword {
    @EmailConstraint(message = "Email không hợp lệ")
    String email;
}