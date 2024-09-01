package com.project.pharmacy.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyCreateRequest {
    @NotNull(message = "INVALID_COMPANY_NAME")
    String name;

    @NotNull(message = "INVALID_COMPANY_ORIGIN")
    String origin;
}
