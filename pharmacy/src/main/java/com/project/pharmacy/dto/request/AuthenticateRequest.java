package com.project.pharmacy.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticateRequest {
    @NotNull(message = "Vui lòng nhập tên đăng nhập")
    String username;

    @NotNull(message = "Vui lòng nhập mật khẩu")
    String password;
}
