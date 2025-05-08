package com.project.pharmacy.dto.request.category;

import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryCreateRequest {
    @NotNull(message = "Please fill out Category Name")
    String name;

    String description;
    String parent;
}
