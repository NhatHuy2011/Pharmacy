package com.project.pharmacy.service.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.pharmacy.configuration.ChatRealTimeHandler;
import com.project.pharmacy.dto.request.chatmessage.CreateChatMessageRequest;
import com.project.pharmacy.dto.request.chatroom.ChooseRoomVacant;
import com.project.pharmacy.dto.request.chatroom.CreateChatRoomRequest;
import com.project.pharmacy.dto.response.entity.ChatMessageResponse;
import com.project.pharmacy.dto.response.entity.ChatRoomResponse;
import com.project.pharmacy.entity.ChatMessage;
import com.project.pharmacy.entity.ChatRoom;
import com.project.pharmacy.entity.Employee;
import com.project.pharmacy.entity.User;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.ChatMessageRepository;
import com.project.pharmacy.repository.ChatRoomRepository;
import com.project.pharmacy.repository.EmployeeRepository;
import com.project.pharmacy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatService {
    ChatRoomRepository chatRoomRepository;

    ChatMessageRepository chatMessageRepository;

    EmployeeRepository employeeRepository;

    UserRepository userRepository;

    ChatRealTimeHandler chatRealTimeHandler;

    public List<ChatRoomResponse> getChatRoomUser(){
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<ChatRoom> chatRooms = chatRoomRepository.findAllBySenderId(user.getId());

        List<ChatRoomResponse> chatRoomResponses = chatRooms.stream()
                .map(chatRoom -> {
                    String receiverId = chatRoom.getReceiverId();
                    Employee employee = null;
                    if (receiverId != null) {
                        employee = employeeRepository.findById(receiverId).orElse(null);
                    }

                    return ChatRoomResponse.builder()
                            .roomId(chatRoom.getId())
                            .receiverId(receiverId)
                            .receiverName(employee != null ? employee.getFirstname() + " " + employee.getLastname() : "Unknown")
                            .receiverImage(employee != null ? employee.getImage() : null)
                            .lastMessage(chatRoom.getLastMessage())
                            .lastTime(chatRoom.getLastTime())
                            .build();
                })
                .toList();

        return chatRoomResponses;
    }


    public List<ChatRoomResponse> getChatRoomEmployee(){
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<ChatRoom> chatRooms = chatRoomRepository.findAllByReceiverId(employee.getId());

        List<ChatRoomResponse> chatRoomResponses = chatRooms.stream()
                .map(chatRoom -> {
                    User user = userRepository.findById(chatRoom.getSenderId())
                            .orElseGet(null);

                    return ChatRoomResponse.builder()
                            .roomId(chatRoom.getId())
                            .senderId(chatRoom.getSenderId())
                            .senderName(user.getFirstname() + " " + user.getLastname())
                            .senderImage(user.getImage())
                            .lastMessage(chatRoom.getLastMessage())
                            .lastTime(chatRoom.getLastTime())
                            .build();
                })
                .toList();

        return chatRoomResponses;
    }

    public List<ChatMessageResponse> getListMessageAtRoom(String roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        User user = userRepository.findById(chatRoom.getSenderId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String receiverId = chatRoom.getReceiverId();
        Employee employee;
        if (receiverId != null) {
            employee = employeeRepository.findById(receiverId)
                    .orElse(null);
        } else {
            employee = null;
        }

        List<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoomOrderByTimeAsc(chatRoom);

        List<ChatMessageResponse> chatMessageResponses = chatMessages.stream()
                .map(chatMessage -> {
                    String senderName;
                    String senderImage;
                    String receiverName;
                    String receiverImage;

                    if (chatMessage.getSenderId().equals(user.getId())){
                        senderName = user.getFirstname() + " " + user.getLastname();
                        senderImage = user.getImage();
                        receiverName = null;
                        receiverImage = null;
                    } else {
                        assert employee != null;
                        senderName = employee.getFirstname() + " " + employee.getLastname();
                        senderImage = employee.getImage();
                        receiverName = user.getFirstname() + " " + user.getLastname();
                        receiverImage = user.getImage();
                    }
                    return ChatMessageResponse.builder()
                            .messageId(chatMessage.getId())
                            .senderId(chatMessage.getSenderId())
                            .senderName(senderName)
                            .senderImage(senderImage)
                            .receiverId(chatMessage.getReceiverId())
                            .receiverName(receiverName)
                            .receiverImage(receiverImage)
                            .content(chatMessage.getContent())
                            .time(chatMessage.getTime())
                            .build();
                })
                .toList();

        return chatMessageResponses;
    }

    public ChatMessageResponse createMessageAtRoom(CreateChatMessageRequest request) throws IOException {
        ChatRoom chatRoom = chatRoomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(request.getSender())
                .receiverId(request.getReceiver())
                .content(request.getContent())
                .time(LocalDateTime.now())
                .chatRoom(chatRoom)
                .build();
        chatMessageRepository.save(chatMessage);

        chatRoom.setLastMessage(chatMessage.getContent());
        chatRoom.setLastTime(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);

        chatRoom.getMessages().add(chatMessage);

        //Parse message to json
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String payload = objectMapper.writeValueAsString(request);

        for (Map.Entry<String, WebSocketSession> entry : chatRealTimeHandler.getOccupiedSessions().entrySet()) {
            if (entry.getKey().equals(request.getSender())) {
                entry.getValue().sendMessage(new TextMessage(payload));
            }
            if (entry.getKey().equals(request.getReceiver())){
                entry.getValue().sendMessage(new TextMessage(payload));
            }
        }

        ChatMessageResponse chatMessageResponse = ChatMessageResponse.builder()
                .messageId(chatMessage.getId())
                .senderId(chatMessage.getSenderId())
                .receiverId(chatMessage.getReceiverId())
                .content(chatMessage.getContent())
                .time(LocalDateTime.now())
                .build();

        return chatMessageResponse;
    }

    public ChatRoomResponse createNewRoom(CreateChatRoomRequest request) throws IOException {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        ChatRoom chatRoom = ChatRoom.builder()
                .senderId(user.getId())
                .lastMessage(request.getContent())
                .lastTime(LocalDateTime.now())
                .roomStatus(true)
                .build();
        chatRoomRepository.save(chatRoom);

        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(user.getId())
                .content(request.getContent())
                .chatRoom(chatRoom)
                .time(LocalDateTime.now())
                .build();
        chatMessageRepository.save(chatMessage);

        ChatRoomResponse chatRoomResponse = ChatRoomResponse.builder()
                .roomId(chatRoom.getId())
                .senderId(chatRoom.getSenderId())
                .senderName(user.getFirstname() + " " + user.getLastname())
                .senderImage(user.getImage())
                .roomStatus(chatRoom.getRoomStatus())
                .lastMessage(chatRoom.getLastMessage())
                .lastTime(chatRoom.getLastTime())
                .build();

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String payload = objectMapper.writeValueAsString(chatRoomResponse);

        for (Map.Entry<String, WebSocketSession> employees : chatRealTimeHandler.getEmployeeSessions().entrySet()){
            employees.getValue().sendMessage(new TextMessage(payload));
        }

        return chatRoomResponse;
    }

    @PreAuthorize("hasRole('NURSE')")
    public List<ChatRoomResponse> getChatRoomVacant(){

        List<ChatRoom> chatRooms = chatRoomRepository.findByRoomStatus(true);

        List<ChatRoomResponse> chatRoomResponses = chatRooms.stream()
                .map(chatRoom -> {
                    User user = userRepository.findById(chatRoom.getSenderId())
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

                    return ChatRoomResponse.builder()
                            .roomId(chatRoom.getId())
                            .senderId(user.getId())
                            .senderName(user.getFirstname() + " " + user.getLastname())
                            .senderImage(user.getImage())
                            .lastMessage(chatRoom.getLastMessage())
                            .lastTime(chatRoom.getLastTime())
                            .roomStatus(chatRoom.getRoomStatus())
                            .build();
                })
                .toList();

        return chatRoomResponses;
    }

    @PreAuthorize("hasRole('NURSE')")
    public void chooseRoomVacant(ChooseRoomVacant roomVacant){
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        Employee employee = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findById(roomVacant.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        chatRoom.setReceiverId(employee.getId());
        chatRoom.setRoomStatus(false);

        chatRoomRepository.save(chatRoom);
    }
}
