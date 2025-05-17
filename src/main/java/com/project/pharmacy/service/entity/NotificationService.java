package com.project.pharmacy.service.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.pharmacy.configuration.WebsocketHandler;
import com.project.pharmacy.dto.request.notification.CreateNotificationRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    WebsocketHandler websocketHandler;

    public void sendNotification(CreateNotificationRequest request) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String payload = objectMapper.writeValueAsString(request);
        for (WebSocketSession session : websocketHandler.getConnections()) {
            session.sendMessage(new TextMessage(payload));
        }
    }
}
