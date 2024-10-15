package com.project.pharmacy.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.project.pharmacy.validator.DobConstraint;
import com.project.pharmacy.validator.EmailConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreateRequest {
    @Size(min = 3, message = "Tên đăng nhập phải có ít nhất 3 kí tự")
    String username;

    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 kí tự")
    String password;

    String confirmPassword;

    String firstname;
    String lastname;

    @DobConstraint(min = 18, message = "Bạn phải đủ 18 tuổi để sử dụng trang web")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate dob;

    String sex;
    Integer phone_number;

    @NotNull(message = "Vui lòng điền email")
    @EmailConstraint(message = "Email không đúng định dạng")
    String email;
}
