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
    @NotNull(message = "INVALID_PRODUCT_NAME")
    String name;

    @NotNull(message = "INVALID_PRODUCT_QUANTITY")
    int quantity;

    String categoryId;

    @NotNull(message = "INVALID_PRODUCT_BENEFITS")
    String benefits;

    @NotNull(message = "INVALID_PRODUCT_INGREDIENTS")
    String ingredients;

    @NotNull(message = "INVALID_PRODUCT_CONSTRAINDICATION")
    String constraindication;

    @NotNull(message = "INVALID_PRODUCT_OBJECT_USE")
    String object_use;

    @NotNull(message = "INVALID_PRODUCT_INSTRUCTION")
    String instruction;

    @NotNull(message = "INVALID_PRODUCT_PRESERVE")
    String preserve;

    String description;
    String note;

    @NotNull(message = "INVALID_PRODUCT_ADVICE")
    boolean doctor_advice;

    String companyId;
}
