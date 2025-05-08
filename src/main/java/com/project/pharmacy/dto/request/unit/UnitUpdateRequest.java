package com.project.pharmacy.dto.request.unit;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UnitUpdateRequest {
    String id;
    String name;
    String description;
}
