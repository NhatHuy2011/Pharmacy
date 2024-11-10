package com.project.pharmacy.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAddressRequest {
    @NotNull(message = "Vui lòng điền họ và tên")
    String fullname;

    @NotNull(message = "Vui lòng điền số điện thoại")
    int phone;

    @NotNull(message = "Vui lòng điền thông tin tỉnh/thành phố")
    String province;

    @NotNull(message = "Vui lòng điền thông tin quận/huyện")
    String district;

    @NotNull(message = "Vui lòng điền thông tin phường/xã")
    String village;

    @NotNull(message = "Vui lòng điền thông tin địa chỉ. Ví dụ: số nhà, đường...")
    String address;

    String addressCategory;

    Boolean addressDefault;
}
