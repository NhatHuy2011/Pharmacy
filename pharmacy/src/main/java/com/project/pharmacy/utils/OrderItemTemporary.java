package com.project.pharmacy.utils;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderItemTemporary {
    String id;
    String priceId;
    String productName;
    String unitName;
    int quantity;
    int price;
}
