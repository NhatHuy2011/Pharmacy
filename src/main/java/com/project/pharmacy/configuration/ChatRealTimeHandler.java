package com.project.pharmacy.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.pharmacy.dto.request.chatmessage.CreateChatMessageRequest;
import com.project.pharmacy.entity.ChatMessage;
import com.project.pharmacy.entity.ChatRoom;
import com.project.pharmacy.entity.Employee;
import com.project.pharmacy.entity.User;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.ChatRoomRepository;
import com.project.pharmacy.repository.EmployeeRepository;
import com.project.pharmacy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatRealTimeHandler extends TextWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(ChatRealTimeHandler.class);
    @Getter
    Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Getter
    Map<String, WebSocketSession> employeeSessions = new ConcurrentHashMap<>();

    @Getter
    Map<String, WebSocketSession> occupiedSessions = new ConcurrentHashMap<>();

    UserRepository userRepository;

    EmployeeRepository employeeRepository;

    ChatRoomRepository chatRoomRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        URI uri = session.getUri();
        MultiValueMap<String, String> params = UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams();

        String username = params.getFirst("username");
        String role = params.getFirst("role");

        if(role.equals("ROLE_NURSE")){
            Employee employee = employeeRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND));
            employeeSessions.put(employee.getId(), session);
        } else {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            userSessions.put(user.getId(), session);
        }

        for(Map.Entry<String, WebSocketSession> employees : employeeSessions.entrySet()){
            for (Map.Entry<String, WebSocketSession> users : userSessions.entrySet()){
                if(chatRoomRepository.existsBySenderIdAndReceiverId(users.getKey(), employees.getKey())){
                    occupiedSessions.put(employees.getKey(), employees.getValue());
                    occupiedSessions.put(users.getKey(), users.getValue());
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        for (Map.Entry<String, WebSocketSession> entry : occupiedSessions.entrySet()) {
            if (entry.getValue().getId().equals(session.getId())) {
                occupiedSessions.remove(entry.getKey());
            }
        }

        for (Map.Entry<String, WebSocketSession> entry : userSessions.entrySet()) {
            if (entry.getValue().getId().equals(session.getId())) {
                userSessions.remove(entry.getKey());
            }
        }

        for (Map.Entry<String, WebSocketSession> entry : employeeSessions.entrySet()) {
            if (entry.getValue().getId().equals(session.getId())) {
                employeeSessions.remove(entry.getKey());
            }
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws IOException {

    }
}
