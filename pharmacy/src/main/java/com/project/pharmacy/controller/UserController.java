package com.project.pharmacy.controller;

import com.project.pharmacy.dto.request.*;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.UserResponse;
import com.project.pharmacy.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
                .message("Please enter the OTP code sent via email to register an account")
                .build();
    }

    @PostMapping("/verify-otp")
    public ApiResponse<Void> verifyOTP(@RequestParam("email") String email,
                                          @RequestParam("otp") String otp){
        userService.verifyOtp(email, otp);
        return ApiResponse.<Void>builder()
                .message("User sign up successful. Please sign in!")
                .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@RequestParam("email") String email){
        userService.forgotPassword(email);
        return ApiResponse.<Void>builder()
                .message("OTP has been sent to your email. Please check.")
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestParam("email") String email,
                                           @RequestParam("otp") String otp,
                                           @RequestParam("newPassword") String newPassword){
        userService.resetPassword(email, otp, newPassword);
        return ApiResponse.<Void>builder()
                .message("Password has been reset successfully. Please log in with your new password.")
                .build();
    }

    @PostMapping("/create-password")
    public ApiResponse<Void> createPassword(@RequestBody @Valid PasswordCreateRequest request){
        userService.creatPassword(request);
        return ApiResponse.<Void>builder()
                .message("Password has been created, you could use it to log-in")
                .build();
    }

    @PostMapping("/update-password")
    public ApiResponse<Void> updatePassword(@Valid @RequestBody UserUpdatePassword request){
        userService.updatePassword(request);
        return ApiResponse.<Void>builder()
                .message("Password renew successful")
                .build();
    }

    @GetMapping
    public ApiResponse<Page<UserResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> userResponses = userService.getAll(pageable);
        return ApiResponse.<Page<UserResponse>>builder()
                .result(userResponses)
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

    @PutMapping("{id}")
    public ApiResponse<Void> banUser(@PathVariable String id){
        userService.banUser(id);
        return ApiResponse.<Void>builder()
                .message("User has been banned")
                .build();
    }
}
