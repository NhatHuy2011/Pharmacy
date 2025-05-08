package com.project.pharmacy.dto.request.order;

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
public class CreateOrderRequestAtCartGuest {
    @NotNull(message = "Vui lòng điền tên người nhận")
    String fullname;
    @NotNull(message = "Vui lòng điền số điện thoại")
    int phone;
    @NotNull(message = "Vui lòng chọn tỉnh")
    int province;
    @NotNull(message = "Vui lòng chọn huyện")
    int district;
    @NotNull(message = "Vui lòng chọn xã")
    String village;
    @NotNull(message = "Vui lòng nhập địa chỉ")
    String address;

    AddressCategory addressCategory;

    @NotNull(message = "Vui lòng chọn phương thức thanh toán")
    PaymentMethod paymentMethod;

    Boolean isInsurance;
    int service_id;
}
