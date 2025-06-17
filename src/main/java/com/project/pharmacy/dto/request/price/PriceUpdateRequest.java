package com.project.pharmacy.dto.request.price;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriceUpdateRequest {
    String priceId;
    String productId;
    int price;
    int quantity;
    String description;
}
