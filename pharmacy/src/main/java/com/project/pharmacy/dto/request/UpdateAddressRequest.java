package com.project.pharmacy.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateAddressRequest {
    String id;
    String fullname;
    int phone;
    String province;
    String district;
    String village;
    String address;
    String addressCategory;
    Boolean addressDefault;
}
