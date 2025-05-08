package com.project.pharmacy.dto.request.company;

import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyCreateRequest {
    @NotNull(message = "Please fill out Company name")
    String name;

    @NotNull(message = "Please fill out Company Origin")
    String origin;
}
