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
    @Size(min = 8, message = "INVALID_PASSWORD")
    String oldPassword;
    @Size(min = 8, message = "INVALID_PASSWORD")
    String newPassword;
    @Size(min = 8, message = "INVALID_PASSWORD")
    String checkNewPassword;
}
