package com.project.pharmacy.dto.request.user;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserVerifiedEmailSignUp {
    String email;
    String otp;
}
