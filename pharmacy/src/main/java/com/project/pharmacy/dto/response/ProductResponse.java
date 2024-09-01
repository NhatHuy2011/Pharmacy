package com.project.pharmacy.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    String id;
    String name;
    List<Integer> price;
    int quantity;
    String category;
    List<String> unit;
    String benefits;
    String ingredients;
    String constraindication;
    String object_use;
    String instruction;
    String preserve;
    String description;
    String note;
    boolean doctor_advice;
    String company;
    List<String> images;
    String image;
}
