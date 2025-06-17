package com.project.pharmacy.dto.request.product;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.pharmacy.validator.DateExpirationConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreateRequest {
    @NotNull(message = "Vui lòng nhập tên sản phẩm")
    String name;

    @NotNull(message = "Vui lòng chọn danh mục cho sản phẩm")
    String categoryId;

    @NotNull(message = "Vui lòng nhập công dụng của sản phẩm")
    String benefits;

    @NotNull(message = "Vui lòng nhập thành phần của sản phẩm")
    String ingredients;

    String constraindication;

    @NotNull(message = "Vui lòng nhập đối tượng sử dụng của sản phẩm")
    String object_use;

    @NotNull(message = "Vui lòng nhập hướng dẫn sử dụng của sản phẩm")
    String instruction;

    @NotNull(message = "Vui lòng nhập cách bảo quản của sản phẩm")
    String preserve;

    String description;

    String note;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @DateExpirationConstraint
    LocalDate dateExpiration;

    @NotNull(message = "Vui lòng chọn sản phẩm có cần lời khuyên của bác sĩ không")
    boolean doctor_advice;

    @NotNull(message = "Vui lòng chọn công ty cho sản phẩm")
    String companyId;
}
