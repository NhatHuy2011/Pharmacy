package com.project.pharmacy.utils;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CartItemTemporary {
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    String priceId;
    String productName;
    String unitName;
    int price;
    int quantity;
}
