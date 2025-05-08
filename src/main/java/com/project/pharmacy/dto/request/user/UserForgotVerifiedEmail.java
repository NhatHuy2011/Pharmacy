package com.project.pharmacy.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserForgotVerifiedEmail {
    String email;
    String otp;
}
