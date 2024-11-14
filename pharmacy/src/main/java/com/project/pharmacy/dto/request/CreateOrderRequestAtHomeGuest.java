package com.project.pharmacy.dto.request;

import com.project.pharmacy.enums.AddressCategory;
import com.project.pharmacy.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CreateOrderRequestAtHomeGuest {
    String productId;
    String unitId;

    @NotNull(message = "Vui lòng điền tên người nhận")
    String fullname;
    @NotNull(message = "Vui lòng điền số điện thoại")
    String phone;
    @NotNull(message = "Vui lòng chọn tỉnh")
    String province;
    @NotNull(message = "Vui lòng chọn huyện")
    String district;
    @NotNull(message = "Vui lòng chọn xã")
    String village;
    @NotNull(message = "Vui lòng nhập địa chỉ")
    String address;

    AddressCategory addressCategory;

    @NotNull(message = "Vui lòng chọn phương thức thanh toán")
    PaymentMethod paymentMethod;
}
