package com.project.pharmacy.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WhistListResponse {
    String id;
    String userId;
    String productId;
    String productName;
    String image;
}
