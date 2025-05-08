package com.project.pharmacy.dto.request.product;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateRequest {
    String id;
    String name;
    int price;
    int quantity;
    String unitId;
    String categoryId;
    String benefits;
    String ingredients;
    String constraindication;
    String object_use;
    String instruction;
    String preserve;
    String description;
    String note;
    boolean doctor_advice;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate dateExpiration;

    String companyId;
}
