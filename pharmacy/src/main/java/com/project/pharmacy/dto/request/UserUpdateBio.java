package com.project.pharmacy.dto.request;

import java.time.LocalDate;

import com.project.pharmacy.validator.DobConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateBio {
    String id;
    String username;
    String password;
    String fisrtname;
    String lastname;

    @DobConstraint(min = 18, message = "Your age must be at least 18")
    LocalDate dob;

    String sex;
    int phone_number;
    String email;
}
