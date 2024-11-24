package com.project.pharmacy.dto.request;

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
    String description;
}
