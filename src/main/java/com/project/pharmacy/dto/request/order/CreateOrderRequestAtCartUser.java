package com.project.pharmacy.dto.request.order;

import com.project.pharmacy.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderRequestAtCartUser {
    String couponId;
    String addressId;
    @NotNull(message = "Vui lòng chọn phương thức thanh toán")
    PaymentMethod paymentMethod;
    Boolean isInsurance;
    int service_id;
}
