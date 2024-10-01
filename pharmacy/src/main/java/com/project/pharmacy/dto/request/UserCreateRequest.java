package com.project.pharmacy.dto.request;

import java.time.LocalDate;

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
    @Size(min = 3, message = "Username must has at least 3 characters")
    String username;

    @Size(min = 8, message = "Password must has at least 8 characters")
    String password;

    String firstname;
    String lastname;

    @DobConstraint(min = 18, message = "Your age must be at least 18")
    LocalDate dob;

    String sex;
    Integer phone_number;

    @NotNull(message = "Please fill out email")
    @EmailConstraint(message = "Invalid Email")
    String email;

    String image;
}
