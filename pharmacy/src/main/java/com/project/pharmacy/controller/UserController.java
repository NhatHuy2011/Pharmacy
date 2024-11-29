package com.project.pharmacy.controller;

import java.io.IOException;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.project.pharmacy.dto.request.*;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.UserResponse;
import com.project.pharmacy.service.UserService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/user")
public class UserController {
    UserService userService;

    // Role USER
    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .message("Vui lòng nhập mã OTP được gửi qua email của bạn")
                .build();
    }

    @PutMapping("/verify-email-signup")
    public ApiResponse<Void> verifyEmail(@RequestBody UserVerifiedEmailSignUp request) {
        userService.verifyEmailSignUp(request);
        return ApiResponse.<Void>builder()
                .message("Đăng ký tài khoản thành công. Vui lòng đăng nhập bằng tài khoản mới!")
                .build();
    }

    @PutMapping("/refresh-otp")
    public ApiResponse<Void> refreshOtp(@RequestBody UserRefreshOtp request) {
        userService.refreshOtp(request);
        return ApiResponse.<Void>builder()
                .message("OTP đã được gửi lại qua email của bạn. Vui lòng kiểm tra!")
                .build();
    }

    @PutMapping("/forgot-password")
    public ApiResponse<UserResponse> forgotPassword(@RequestBody @Valid UserForgotPassword request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.forgotPassword(request))
                .message("Vui lòng nhập mã OTP được gửi qua email của bạn")
                .build();
    }

    @PutMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@RequestBody @Valid UserResetPassword request) {
        userService.resetPassword(request);
        return ApiResponse.<Void>builder()
                .message("Mật khẩu đã được tạo mới thành công. Vui lòng đăng nhập với mật khẩu mới")
                .build();
    }

    // Login with Google
    @PutMapping("/create-password")
    public ApiResponse<Void> createPassword(@RequestBody @Valid PasswordCreateRequest request) {
        userService.creatPassword(request);
        return ApiResponse.<Void>builder()
                .message("Mật khẩu đã được tạo. Bạn có thể dùng nó để đăng nhập!")
                .build();
    }

    @GetMapping
    public ApiResponse<Page<UserResponse>> getAll(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> userResponses = userService.getAll(pageable);
        return ApiResponse.<Page<UserResponse>>builder().result(userResponses).build();
    }

    @GetMapping("/bio")
    public ApiResponse<UserResponse> getMyInFo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping("/update-bio")
    public ApiResponse<UserResponse> updateBio(
            @RequestPart("updateUser") @Valid UserUpdateBio request,
            @RequestPart(value = "file", required = false) MultipartFile file)
            throws IOException {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateBio(request, file))
                .message("Cập nhật thông tin thành công")
                .build();
    }

    @PutMapping("/update-email")
    public ApiResponse<UserResponse> updateEmail(@RequestBody @Valid UserUpdateEmail request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateEmail(request))
                .message("Vui lòng nhập mã OTP được gửi qua email của bạn")
                .build();
    }

    @PutMapping("/verify-email-update")
    public ApiResponse<Void> verifyEmailUpdate(@RequestBody UserVerifiedEmailUpdate request) {
        userService.verifyEmailUpdate(request);
        return ApiResponse.<Void>builder()
                .message("Email được cập nhật thành công!")
                .build();
    }

    @PutMapping("/forgot-verify-email")
    public ApiResponse<UserResponse> forgotVerifyEmail() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.forgotVerifyEmail())
                .message("Vui lòng nhập mã OTP được gửi qua email của bạn")
                .build();
    }

    @PutMapping("/verify-email-forgot")
    public ApiResponse<Void> verifyEmailForgot(@RequestBody UserForgotVerifiedEmail request) {
        userService.verifyEmailForgot(request);
        return ApiResponse.<Void>builder()
                .message("Email được xác thực thành công!")
                .build();
    }

    @PutMapping("/update-password")
    public ApiResponse<Void> updatePassword(@RequestBody @Valid UserUpdatePassword request) {
        userService.updatePassword(request);
        return ApiResponse.<Void>builder()
                .message("Mật khẩu được tạo mới thành công")
                .build();
    }

    // Role ADMIN
    @PutMapping("/updateRole")
    public ApiResponse<UserResponse> updateRole(@RequestBody UserUpdateRole request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateRole(request))
                .build();
    }

    @PutMapping("/ban/{id}")
    public ApiResponse<Void> banUser(@PathVariable String id) {
        userService.banUser(id);
        return ApiResponse.<Void>builder()
                .message("User has been banned")
                .build();
    }

    @PutMapping("/unban/{id}")
    public ApiResponse<Void> unbanUser(@PathVariable String id) {
        userService.unbanUser(id);
        return ApiResponse.<Void>builder()
                .message("User has been unbanned")
                .build();
    }
}
