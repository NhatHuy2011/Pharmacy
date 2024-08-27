package com.project.pharmacy.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.pharmacy.entity.Category;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {
    String id;
    String name;
    String description;
    String image;
    String parent;
}
