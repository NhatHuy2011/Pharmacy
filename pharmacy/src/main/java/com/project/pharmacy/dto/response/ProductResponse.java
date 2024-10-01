package com.project.pharmacy.dto.response;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    String id;
    String name;
    int quantity;
    String benefits;
    String ingredients;
    String constraindication;
    String object_use;
    String instruction;
    String preserve;
    String description;
    String note;
    boolean doctor_advice;

    CompanyResponse company;

    CategoryResponse category;

    Set<Integer> price_all; // for getAllProduct
    Integer price_one; // for getOneProduct

    Set<String> unit_all; // for getAllProduct
    String unit_one; // for getOneProduct

    List<String> images;
    String image;
}
