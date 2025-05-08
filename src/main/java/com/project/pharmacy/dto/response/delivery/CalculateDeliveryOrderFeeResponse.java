package com.project.pharmacy.dto.response.delivery;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CalculateDeliveryOrderFeeResponse {
    @JsonProperty("total")
    int total;

    @JsonProperty("service_fee")
    int serviceFee;

    @JsonProperty("insurance_fee")
    int insuranceFee;
}
