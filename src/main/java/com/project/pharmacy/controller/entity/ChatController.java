package com.project.pharmacy.controller.entity;

import com.project.pharmacy.dto.request.chatmessage.CreateChatMessageRequest;
import com.project.pharmacy.dto.request.chatroom.ChooseRoomVacant;
import com.project.pharmacy.dto.request.chatroom.CreateChatRoomRequest;
import com.project.pharmacy.dto.response.common.ApiResponse;
import com.project.pharmacy.dto.response.entity.ChatMessageResponse;
import com.project.pharmacy.dto.response.entity.ChatRoomResponse;
import com.project.pharmacy.service.entity.ChatService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatController {
    ChatService chatService;

    @GetMapping("/room/user")
    public ApiResponse<List<ChatRoomResponse>> getChatRoomUser(){
        return ApiResponse.<List<ChatRoomResponse>>builder()
                .result(chatService.getChatRoomUser())
                .build();
    }

    @GetMapping("/room/employee")
    public ApiResponse<List<ChatRoomResponse>> getChatRoomEmployee(){
        return ApiResponse.<List<ChatRoomResponse>>builder()
                .result(chatService.getChatRoomEmployee())
                .build();
    }

    @GetMapping("/room/message")
    public ApiResponse<List<ChatMessageResponse>> getListMessageAtRoom(@RequestParam String roomId){
        return ApiResponse.<List<ChatMessageResponse>>builder()
                .result(chatService.getListMessageAtRoom(roomId))
                .build();
    }

    @GetMapping("/room/vacant")
    public ApiResponse<List<ChatRoomResponse>> getChatRoomVacant(){
        return ApiResponse.<List<ChatRoomResponse>>builder()
                .result(chatService.getChatRoomVacant())
                .build();
    }

    @PutMapping("/room/choose")
    public ApiResponse<Void> chooseRoomVacant(@RequestBody ChooseRoomVacant roomVacant){
        chatService.chooseRoomVacant(roomVacant);
        return ApiResponse.<Void>builder()
                .message("Hãy nói chuyện với khách hàng nhẹ nhàng nhé!")
                .build();
    }

    @PostMapping("/create/message")
    public ApiResponse<ChatMessageResponse> createMessage(@RequestBody CreateChatMessageRequest request) throws IOException {
        return ApiResponse.<ChatMessageResponse>builder()
                .result(chatService.createMessageAtRoom(request))
                .message("Gửi tin nhắn thành công")
                .build();
    }

    @PostMapping("/create/room")
    public ApiResponse<ChatRoomResponse> createNewChatRoom(@RequestBody CreateChatRoomRequest request) throws IOException {
        return ApiResponse.<ChatRoomResponse>builder()
                .result(chatService.createNewRoom(request))
                .message("Gửi tin nhắn thành công")
                .build();
    }
}
