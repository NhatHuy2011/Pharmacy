package com.project.pharmacy.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
