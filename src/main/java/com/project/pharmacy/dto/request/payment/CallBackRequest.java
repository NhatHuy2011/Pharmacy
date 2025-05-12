package com.project.pharmacy.dto.request.payment;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class CallBackRequest {
    int code;
    String orderId;
}
