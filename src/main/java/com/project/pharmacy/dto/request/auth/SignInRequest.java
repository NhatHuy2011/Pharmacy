package com.project.pharmacy.dto.request.auth;

import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignInRequest {
    @NotNull(message = "Vui lòng nhập tên đăng nhập")
    String username;

    @NotNull(message = "Vui lòng nhập mật khẩu")
    String password;
}
