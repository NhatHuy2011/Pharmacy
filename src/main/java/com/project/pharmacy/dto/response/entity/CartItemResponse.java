package com.project.pharmacy.dto.response.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemResponse {
    String id;
    String priceId;
    String productName;
    String unitName;
    int price;
    int quantity;
    int amount;
    String image;
}
