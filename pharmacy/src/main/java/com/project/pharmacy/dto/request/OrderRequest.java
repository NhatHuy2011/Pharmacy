package com.project.pharmacy.dto.request;

import com.project.pharmacy.enums.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    String addressId;
    PaymentMethod paymentMethod;
}
