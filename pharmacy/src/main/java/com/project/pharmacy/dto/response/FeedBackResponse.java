package com.project.pharmacy.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedBackResponse {
    String id;
    String userId;
    String username;
    String avatar;
    String productId;
    String productName;
    String feedback;
    FeedBackResponse parent;
    LocalDate createDate;
}
