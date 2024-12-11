package com.project.pharmacy.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.project.pharmacy.enums.Level;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate dob;

    String sex;
    Integer phone_number;
    String email;
    String image;
    int point;
    Level level;
    Boolean status;
    Boolean noPassword;
    Set<RoleResponse> roles;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime otpExpiryTime;

    Boolean isVerified;
}
