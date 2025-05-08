package com.project.pharmacy.dto.request.coupon;

import com.project.pharmacy.enums.Level;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCouponRequest {
    String id;
    String name;
    String image;
    int percent;
    int max;
    int orderRequire;
    Level levelUser;
    String description;
    LocalDate expireDate;
}
