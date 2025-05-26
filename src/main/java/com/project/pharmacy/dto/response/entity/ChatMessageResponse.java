package com.project.pharmacy.dto.response.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessageResponse {
    String messageId;
    String senderId;
    String senderName;
    String senderImage;
    String receiverId;
    String receiverName;
    String receiverImage;
    String content;
    LocalDateTime time;
}
