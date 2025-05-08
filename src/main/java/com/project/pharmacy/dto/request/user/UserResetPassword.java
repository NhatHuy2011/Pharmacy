package com.project.pharmacy.dto.request.user;

import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "\\d{6}", message = "Mã OTP không đúng định dạng. Mã OTP gồm 6 chữ số đã được gửi qua mail của bạn")
    String otp;

    @Size(message = "Password phải có ít nhất 8 kí tự")
    String newPassword;
}
