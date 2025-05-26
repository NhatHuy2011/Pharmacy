package com.project.pharmacy.dto.request.chatroom;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChooseRoomVacant {
    String roomId;
}
