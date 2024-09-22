package com.project.pharmacy.controller;

import com.project.pharmacy.dto.request.UserCreateRequest;
import com.project.pharmacy.dto.request.UserUpdateBio;
import com.project.pharmacy.dto.request.UserUpdateRole;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.UserResponse;
import com.project.pharmacy.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/user")
public class UserController {
    UserService userService;

    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestPart("createUser") UserCreateRequest request,
                                                @RequestPart(value = "file") MultipartFile file) throws IOException{
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request, file))
                .build();
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> getAll(){
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAll())
                .build();
    }

    @GetMapping("/myInfo")
    public ApiResponse<UserResponse> getMyInFo(){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/updateBio")
    public ApiResponse<UserResponse> updateBio(@RequestPart("updateUser") UserUpdateBio request,
                                               @RequestPart("file") MultipartFile file) throws IOException{
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateBio(request, file))
                .build();
    }

    @PutMapping("/updateRole")
    public ApiResponse<UserResponse> updateRole(@RequestBody UserUpdateRole request){
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateRole(request))
                .build();
    }
}
