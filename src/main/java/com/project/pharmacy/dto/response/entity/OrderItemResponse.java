package com.project.pharmacy.dto.response.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    String id;
    String productId;
    String priceId;
    String productName;
    String unitName;
    int quantity;
    int price;
    int amount;
    String image;
}
