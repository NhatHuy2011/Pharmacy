package com.project.pharmacy.dto.request.employee;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateEmployeeRequest {
    @Size(min = 3, message = "Tên đăng nhập phải có ít nhất 3 kí tự")
    String username;

    @Size(min = 8, message = "Mật khẩu phải có ít nhất 8 kí tự")
    String password;

    String confirmPassword;

    @NotNull(message = "Vui lòng nhập tên nhân viên")
    String firstname;

    @NotNull(message = "Vui lòng nhập họ nhân viên")
    String lastname;

    @NotNull(message = "Vui lòng nhập tuổi nhân viên")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate dob;

    @NotNull(message = "Vui lòng nhập số điện thoại nhân viên")
    String phoneNumber;

    @NotNull(message = "Vui lòng nhập giới tính")
    String sex;

    @NotNull(message = "Vui lòng nhập chuyên ngành")
    String specilization;

    @NotNull(message = "Vui lòng nhập mô tả")
    String description;

    @NotNull(message = "Vui lòng nhập kinh nghiệm làm việc")
    String workExperience;

    @NotNull(message = "Vui lòng nhập quá trình học tập")
    String education;

    @NotNull(message = "Vui lòng nhập thời gian công tác")
    int workTime;

    @NotNull(message = "Vui lòng nhập mức lương")
    int salary;

    @NotNull
    String role;
}
