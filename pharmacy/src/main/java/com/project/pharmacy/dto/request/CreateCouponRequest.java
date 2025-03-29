package com.project.pharmacy.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.pharmacy.enums.Level;
import com.project.pharmacy.validator.DateExpirationConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateCouponRequest {
    @NotNull(message = "Vui lòng điền tên mã giảm giá")
    String name;

    @NotNull(message = "Vui lòng điền giá trị mã giảm")
    int percent;

    @NotNull(message = "Vui lòng điền giá trị tối đa của mã giảm")
    int max;

    @NotNull(message = "Vui lòng điền giá trị đơn hàng được áp dụng")
    int orderRequire;

    @NotNull(message = "Vui lòng điền cấp độ áp dụng cho user")
    Level levelUser;

    @NotNull(message = "Vui lòng điền thông tin miêu tả")
    String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @NotNull(message = "Vui lòng điền ngày hết hạn")
    @DateExpirationConstraint
    LocalDate expireDate;
}
