package com.project.pharmacy.dto.request.order;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmOrderForNurse {
    boolean confirm;
    String orderId;
}
