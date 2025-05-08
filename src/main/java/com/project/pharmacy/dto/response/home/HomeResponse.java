package com.project.pharmacy.dto.response.home;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.pharmacy.dto.response.entity.CategoryResponse;
import com.project.pharmacy.dto.response.entity.CompanyResponse;
import com.project.pharmacy.dto.response.entity.ProductResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HomeResponse {
    List<CategoryResponse> categories;
    List<ProductResponse> newProducts;
    List<ProductResponse> topProducts;
    List<CompanyResponse> topCompanies;
}
