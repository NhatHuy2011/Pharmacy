package com.project.pharmacy.dto.request;

import com.project.pharmacy.validator.EmailConstraint;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResetPassword {
    String email;

    String otp;

    @Size(message = "Password phải có ít nhất 8 kí tự")
    String newPassword;
}
