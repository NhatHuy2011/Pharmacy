package com.project.pharmacy.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    String id;
    String username;
    String firstname;
    String lastname;
    LocalDate dob;
    String sex;
    Integer phone_number;
    String email;
    String image;
    int point;
    Boolean status;
    Boolean noPassword;
    Set<RoleResponse> roles;
    String otpCode;
    LocalDateTime otpExpiryTime;
    Boolean isVerified;
}
