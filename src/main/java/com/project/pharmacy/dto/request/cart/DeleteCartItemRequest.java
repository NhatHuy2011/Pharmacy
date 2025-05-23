package com.project.pharmacy.dto.request.cart;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteCartItemRequest {
    String cartItemId;
}
