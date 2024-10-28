package com.project.pharmacy.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartTemporary {
    private List<CartItemTemporary> cartItems = new ArrayList<>();
    private int totalPrice;
}
