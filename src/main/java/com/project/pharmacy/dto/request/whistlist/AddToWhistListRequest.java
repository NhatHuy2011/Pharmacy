package com.project.pharmacy.dto.request.whistlist;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddToWhistListRequest {
    String productId;
}
