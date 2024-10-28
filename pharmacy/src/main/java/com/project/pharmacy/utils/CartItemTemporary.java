package com.project.pharmacy.utils;

import lombok.Data;

@Data
public class CartItemTemporary {
    private String productId;
    private String productName;
    private String unitId;
    private String unitName;
    private int price;
    private int quantity;
}
