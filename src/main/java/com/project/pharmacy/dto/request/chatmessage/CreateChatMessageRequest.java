package com.project.pharmacy.dto.request.chatmessage;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateChatMessageRequest {
    String roomId;
    String messageId;
    String sender;
    String receiver;
    String content;
}
