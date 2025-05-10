package com.project.pharmacy.dto.response.delivery;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class AddressDetailResponse {
    ProvinceGHNResponse province;

    DistrictGHNReponse district;

    WardResponse ward;
}
