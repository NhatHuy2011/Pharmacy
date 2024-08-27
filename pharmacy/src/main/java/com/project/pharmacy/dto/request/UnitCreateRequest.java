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
    @NotNull(message = "INVALID_UNIT")
    String name;
    String description;
}
