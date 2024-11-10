package com.project.pharmacy.utils;

import java.util.ArrayList;
import java.util.List;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CartTemporary {
    List<CartItemTemporary> cartItems = new ArrayList<>();
    int totalPrice;
}
