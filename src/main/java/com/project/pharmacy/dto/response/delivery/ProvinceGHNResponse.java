package com.project.pharmacy.dto.response.delivery;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProvinceGHNResponse {
    @JsonProperty("ProvinceID")
    int id;

    @JsonProperty("ProvinceName")
    String name;

    @JsonProperty("Code")
    String code;
}
