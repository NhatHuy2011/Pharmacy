package com.project.pharmacy.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    Integer quantity;
    String benefits;
    String ingredients;
    String constraindication;
    String object_use;
    String instruction;
    String preserve;
    String description;
    String note;
    Boolean doctor_advice;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate dateCreation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate dateExpiration;

    CompanyResponse company;

    CategoryResponse category;

    Set<PriceResponse> prices;

    PriceResponse price;

    List<String> images;

    String image;
}
