package com.project.pharmacy.dto.request.delivery;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class GetAvailableServiceRequest {
    int to_district;
}
