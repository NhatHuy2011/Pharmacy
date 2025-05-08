package com.project.pharmacy.dto.request.user;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    String fisrtname;
    String lastname;

    @DobConstraint(min = 18, message = "Bạn phải đủ 18 tuổi để sử dụng trang web")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate dob;

    String sex;
    int phone_number;
}
