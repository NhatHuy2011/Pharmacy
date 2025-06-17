package com.project.pharmacy.controller.entity;

import com.project.pharmacy.dto.request.auth.ForgotPasswordRequest;
import com.project.pharmacy.dto.request.auth.ForgotVerifyEmailRequest;
import com.project.pharmacy.dto.request.auth.RefreshOTP;
import com.project.pharmacy.dto.request.auth.SignUpRequest;
import com.project.pharmacy.dto.request.oauth.PasswordCreateRequest;
import com.project.pharmacy.dto.request.user.*;
import com.project.pharmacy.dto.response.common.ApiResponse;
import com.project.pharmacy.dto.response.entity.UserResponse;
import com.project.pharmacy.dto.response.statistic.DaylyStatisticResponse;
import com.project.pharmacy.dto.response.statistic.MonthlyStatisticResponse;
import com.project.pharmacy.dto.response.statistic.YearlyStatisticResponse;
import com.project.pharmacy.service.entity.UserService;
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
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/user")
public class UserController {
    UserService userService;

    // Role USER
    @PostMapping
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid SignUpRequest request) {
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
    public ApiResponse<Void> refreshOtp(@RequestBody RefreshOTP request) {
        userService.refreshOtp(request);
        return ApiResponse.<Void>builder()
                .message("OTP đã được gửi lại qua email của bạn. Vui lòng kiểm tra!")
                .build();
    }

    @PutMapping("/forgot-password")
    public ApiResponse<UserResponse> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
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

    //Cập nhật Email cho User đã xác thực -> User
    @PutMapping("/update-email")
    public ApiResponse<UserResponse> updateEmail(@RequestBody @Valid UserUpdateEmail request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateEmail(request))
                .message("Vui lòng nhập mã OTP được gửi qua email của bạn")
                .build();
    }

    //Xác thực Email khi cập nhật Email cho User -> User
    @PutMapping("/verify-email-update")
    public ApiResponse<Void> verifyEmailUpdate(@RequestBody UserVerifiedEmailUpdate request) {
        userService.verifyEmailUpdate(request);
        return ApiResponse.<Void>builder()
                .message("Email được cập nhật thành công!")
                .build();
    }

    //Gửi mã OTP cho User quên xác thực email -> Guest
    @PutMapping("/forgot-verify-email")
    public ApiResponse<UserResponse> forgotVerifyEmail(@RequestBody ForgotVerifyEmailRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.forgotVerifyEmail(request))
                .message("Vui lòng nhập mã OTP được gửi qua email của bạn")
                .build();
    }

    //Xác thực Email dành cho User quên xác thực Email -> Guest
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

    @GetMapping("/spending/date")
    public ApiResponse<List<DaylyStatisticResponse>> spendingHealthByDate(@RequestParam("date") LocalDate date){
        return ApiResponse.<List<DaylyStatisticResponse>>builder()
                .result(userService.spendingHealthByDate(date))
                .build();
    }

    @GetMapping("/spending/total/date")
    public ApiResponse<Long> totalSpendingHealthByDate(@RequestParam("date") LocalDate date){
        return ApiResponse.<Long>builder()
                .result(userService.totalSpendingHealthByDate(date))
                .build();
    }

    @GetMapping("/spending/month")
    public ApiResponse<List<MonthlyStatisticResponse>> spendingHealthByMonth(@RequestParam("month") int month,
                                                                             @RequestParam("year") int year){
        return ApiResponse.<List<MonthlyStatisticResponse>>builder()
                .result(userService.spendingHealthByMonth(month, year))
                .build();
    }

    @GetMapping("/spending/total/month")
    public ApiResponse<Long> totalSpendingHealthByMonth(@RequestParam("month") int month,
                                                        @RequestParam("year") int year){
        return ApiResponse.<Long>builder()
                .result(userService.totalSpendingHealthByMonth(month, year))
                .build();
    }

    @GetMapping("/spending/year")
    public ApiResponse<List<YearlyStatisticResponse>> spendingHealthByYear(@RequestParam("year") int year){
        return ApiResponse.<List<YearlyStatisticResponse>>builder()
                .result(userService.spendingHealthByYear(year))
                .build();
    }

    @GetMapping("/spending/total/year")
    public ApiResponse<Long> totalSpendingHealthByYear(@RequestParam("year") int year){
        return ApiResponse.<Long>builder()
                .result(userService.totalSpendingHealthByYear(year))
                .build();
    }

    @GetMapping("/over/quantity")
    public ApiResponse<Void> sendEmailWhenPriceOverQuantity(@RequestParam String email){
        userService.sendEmailWhenPriceOverQuantity(email);
        return ApiResponse.<Void>builder()
                .message("Vui lòng check email của bạn")
                .build();
    }

    // Role ADMIN
    @GetMapping
    public ApiResponse<Page<UserResponse>> getAll(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> userResponses = userService.getAllUser(pageable);
        return ApiResponse.<Page<UserResponse>>builder()
                .result(userResponses)
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

    //ROLE NURSE
    @GetMapping("/order/shop")
    public ApiResponse<UserResponse> getInfoUserByPhone(@RequestParam String phone){
        return ApiResponse.<UserResponse>builder()
                .result(userService.getInfoUserByPhone(phone))
                .build();
    }
}
