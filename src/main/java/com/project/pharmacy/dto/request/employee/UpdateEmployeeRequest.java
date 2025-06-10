package com.project.pharmacy.dto.request.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEmployeeRequest {
    String id;
    String firstname;
    String lastname;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate dob;
    String sex;
    String phoneNumber;
    String role;
    String specilization;
    String description;
    String workExperience;
    String education;
    int workTime;
    int salary;
}
