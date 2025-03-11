package com.project.pharmacy.utils;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CartItemTemporary {
    String id;
    String priceId;
    String productName;
    String unitName;
    int price;
    int quantity;
    int amount;
    String image;
}
