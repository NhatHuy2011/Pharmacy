package com.project.pharmacy.dto.request.delivery;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CalculateExpectedDeliveryTimeRequest {
    int from_district_id;

    String from_ward_code;

    int to_district_id;

    String to_ward_code;

    int service_id;
}
