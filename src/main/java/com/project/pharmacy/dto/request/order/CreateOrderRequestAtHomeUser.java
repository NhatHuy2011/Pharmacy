package com.project.pharmacy.dto.request.order;

import com.project.pharmacy.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CreateOrderRequestAtHomeUser {
    String couponId;
    String priceId;
    String addressId;
    @NotNull(message = "Vui lòng chọn phương thức thanh toán")
    PaymentMethod paymentMethod;
    Boolean isInsurance;
    int service_id;
}
