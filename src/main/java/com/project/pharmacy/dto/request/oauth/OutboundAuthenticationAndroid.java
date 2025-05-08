package com.project.pharmacy.dto.request.oauth;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OutboundAuthenticationAndroid {
    String email;
    String familyName;
    String givenName;
    String photo;
}
