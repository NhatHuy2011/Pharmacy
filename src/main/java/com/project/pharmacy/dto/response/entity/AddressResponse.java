package com.project.pharmacy.dto.response.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressResponse {
    String id;
    String fullname;
    int phone;
    int province;
    int district;
    String village;
    String address;
    String addressCategory;
    Boolean addressDefault;
}
