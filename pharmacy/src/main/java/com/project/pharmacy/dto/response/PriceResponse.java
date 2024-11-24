package com.project.pharmacy.dto.response;

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
    int price;
    String description;
    String image;
}
