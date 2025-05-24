package com.project.pharmacy.dto.response.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRoomResponse {
    String id;
    String senderId;
    String senderName;
    String senderImage;
    String receiverId;
    String receiverName;
    String receiverImage;
    String title;
    String image;
    String lastMessage;
    int timeBefore;
}
