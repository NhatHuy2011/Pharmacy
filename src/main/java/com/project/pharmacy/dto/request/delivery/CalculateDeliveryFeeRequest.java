package com.project.pharmacy.dto.request.delivery;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CalculateDeliveryFeeRequest {
    int service_id;

    int insurance_value;

    int to_district_id;

    String to_ward_code;
}
