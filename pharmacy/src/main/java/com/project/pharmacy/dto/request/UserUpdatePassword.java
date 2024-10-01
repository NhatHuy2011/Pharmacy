package com.project.pharmacy.dto.request;

import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdatePassword {
    @Size(min = 8, message = "Password must has at least 8 characters")
    String oldPassword;

    @Size(min = 8, message = "Password must has at least 8 characters")
    String newPassword;

    @Size(min = 8, message = "Password must has at least 8 characters")
    String checkNewPassword;
}
