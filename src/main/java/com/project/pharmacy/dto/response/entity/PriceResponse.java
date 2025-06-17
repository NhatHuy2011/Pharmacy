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
public class PriceResponse {
    String id;
    ProductResponse product;
    UnitResponse unit;
    Integer price;
    Integer quantity;
    String description;
    String image;
}
