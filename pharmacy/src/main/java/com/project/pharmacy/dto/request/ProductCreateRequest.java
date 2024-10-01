package com.project.pharmacy.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreateRequest {
    @NotNull(message = "Please fill out Product Name")
    String name;

    @NotNull(message = "Please fill out Product Quantity")
    int quantity;

    String categoryId;

    @NotNull(message = "Please fill out Product Benefits")
    String benefits;

    @NotNull(message = "Please fill out Product Ingredients")
    String ingredients;

    @NotNull(message = "Please fill out Product Constraindication")
    String constraindication;

    @NotNull(message = "Please fill out Product Object Use")
    String object_use;

    @NotNull(message = "Please fill out Product Instruction")
    String instruction;

    @NotNull(message = "Please fill out Product Preserve")
    String preserve;

    String description;

    String note;

    @NotNull(message = "Please fill out Product Doctor Advice")
    boolean doctor_advice;

    String companyId;
}
