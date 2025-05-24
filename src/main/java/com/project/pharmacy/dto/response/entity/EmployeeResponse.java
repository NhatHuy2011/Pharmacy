package com.project.pharmacy.dto.response.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponse {
    String id;
    String username;
    String firstname;
    String lastname;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate dob;
    String sex;
    String image;
    String specilization;
    String description;
    String workExperience;
    String education;
    String phoneNumber;
    int workTime;
    int salary;
    Boolean status;
    RoleResponse role;
}
