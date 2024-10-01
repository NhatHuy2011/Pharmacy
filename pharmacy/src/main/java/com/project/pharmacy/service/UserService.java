package com.project.pharmacy.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.project.pharmacy.dto.request.*;
import com.project.pharmacy.dto.response.UserResponse;
import com.project.pharmacy.entity.Role;
import com.project.pharmacy.entity.User;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.UserMapper;
import com.project.pharmacy.repository.RoleRepository;
import com.project.pharmacy.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;

    ImageService imageService;

    PasswordEncoder passwordEncoder;

    UserMapper userMapper;

    RoleRepository roleRepository;

    EmailService emailService;
    // Role USER
    public UserResponse createUser(UserCreateRequest request, MultipartFile file) throws IOException {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        if (userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.EMAIL_EXISTED);

        String urlImage = imageService.uploadImage(file);

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(false);
        user.setIsVerified(false);
        user.setImage(urlImage);
        user.setRoles(roles);

        String otpCode = generateOTP();
        user.setOtpCode(otpCode);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5)); // Set thời gian hết hạn OTP là 5 phút
        userRepository.save(user);

        // Gửi OTP qua email
        emailService.sendSimpleEmail(
                user.getEmail(), "OTP Verification", "OTP will expire in 5 minutes. OTP is: " + otpCode);

        return userMapper.toUserResponse(user);
    }

    public void verifyOtp(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.getOtpCode().equalsIgnoreCase(otp)) {
            // Kiểm tra thời gian hết hạn OTP
            if (LocalDateTime.now().isBefore(user.getOtpExpiryTime())) {
                user.setStatus(true);
                user.setIsVerified(true);
                user.setOtpCode(null);
                user.setOtpExpiryTime(null);
                userRepository.save(user);
            } else {
                throw new AppException(ErrorCode.OTP_EXPIRED);
            }
        } else {
            throw new AppException(ErrorCode.OTP_INCORRECT);
        }
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));

        String otpCode = generateOTP();
        user.setOtpCode(otpCode);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5)); // Set thời gian hết hạn OTP là 5 phút
        userRepository.save(user);

        emailService.sendSimpleEmail(
                user.getEmail(), "Password Reset OTP", "OTP will expire in 5 minutes. Your OTP is: " + otpCode);
    }

    public void resetPassword(String email, String otp, String newPassword) {
        if (newPassword.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_EXISTED));
        if (user.getOtpCode().equalsIgnoreCase(otp)) {
            if (LocalDateTime.now().isBefore(user.getOtpExpiryTime())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setOtpCode(null);
                user.setOtpExpiryTime(null);
                userRepository.save(user);
            }
            else {
                throw new AppException(ErrorCode.OTP_EXPIRED);
            }
        } else {
            throw new AppException(ErrorCode.OTP_INCORRECT);
        }
    }

    public String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // Mã OTP gồm 6 chữ số
    }

    // User chua co mat khau truoc do (Login with google)
    public void creatPassword(PasswordCreateRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (StringUtils.hasText(user.getPassword()))
            throw new AppException(ErrorCode.PASSWORD_EXISTED);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UserResponse userResponse = userMapper.toUserResponse(user);
        userResponse.setNoPassword(!StringUtils.hasText(user.getPassword()));

        return userResponse;
    }

    @PreAuthorize("returnObject.username == authentication.name")
    public UserResponse updateBio(UserUpdateBio request, MultipartFile file) throws IOException {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        String urlImage = imageService.uploadImage(file);

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userMapper.updateBio(user, request);
        user.setImage(urlImage);
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    // User da co tai khoan truoc do
    public void updatePassword(UserUpdatePassword request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!(passwordEncoder.matches(request.getOldPassword(), user.getPassword()))) // request truoc user ** lỏ vl
        throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);

        if (!(request.getNewPassword().equals(request.getCheckNewPassword()))) {
            throw new AppException(ErrorCode.PASSWORD_RE_ENTERING_INCORRECT);
        }

        user.setPassword(passwordEncoder.encode(request.getCheckNewPassword()));

        userRepository.save(user);
    }

    // Role ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateRole(UserUpdateRole request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        user.getRoles().add(role);
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toUserResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void banUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(false);
        userRepository.save(user);
    }
}
