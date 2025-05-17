package com.project.pharmacy.dto.request.order;

import com.project.pharmacy.enums.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateOrderAtShopRequest {
    String phone;
    List<PriceDTO> listPrices;
    PaymentMethod paymentMethod;
}
