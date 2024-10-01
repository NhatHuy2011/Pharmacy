package com.project.pharmacy.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnitCreateRequest {
    @NotNull(message = "Please fill out Unit Name")
    String name;

    String description;
}
