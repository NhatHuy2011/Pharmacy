package com.project.pharmacy.dto.request.notification;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.pharmacy.enums.Level;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateNotificationRequest {
    String id;
    String title;
    String content;
    String image;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    LocalDate createDate;
    Level level;
}
