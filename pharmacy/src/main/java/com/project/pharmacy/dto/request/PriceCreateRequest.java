package com.project.pharmacy.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceCreateRequest {
    @NotNull(message = "INVALID_PRODUCT_ID")
    String productId;
    @NotNull(message = "INVALID_UNIT_ID")
    String unitId;
    int price;
    String description;
}
