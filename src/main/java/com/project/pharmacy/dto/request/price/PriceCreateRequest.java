package com.project.pharmacy.dto.request.price;

import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceCreateRequest {
    @NotNull(message = "Please fill out Product Info")
    String productId;

    @NotNull(message = "Please fill out Unit Info")
    String unitId;

    int price;

    int quantity;

    String description;
}
