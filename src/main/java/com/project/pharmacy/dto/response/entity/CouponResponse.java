package com.project.pharmacy.dto.response.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.pharmacy.enums.CouponType;
import com.project.pharmacy.enums.Level;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CouponResponse {
    String id;
    String name;
    String image;
    int percent;
    int max;
    int orderRequire;
    Level levelUser;
    CouponType couponType;
    String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate expireDate;
}
