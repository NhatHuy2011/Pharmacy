package com.project.pharmacy.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import com.project.pharmacy.dto.response.DaylyStatisticResponse;
import com.project.pharmacy.dto.response.MonthlyStatisticResponse;
import com.project.pharmacy.dto.response.YearlyStatisticResponse;
import com.project.pharmacy.enums.Level;
import com.project.pharmacy.repository.OrderRepository;
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

    CloudinaryService cloudinaryService;

    PasswordEncoder passwordEncoder;

    UserMapper userMapper;

    RoleRepository roleRepository;

    OrderRepository ordersRepository;

    EmailService emailService;

    // For GUEST
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        if (userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.EMAIL_EXISTED);

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new AppException(ErrorCode.PASSWORD_RE_ENTERING_INCORRECT);

        String otpCode = generateOTP();

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getConfirmPassword()));
        user.setStatus(false);
        user.setIsVerified(false);
        user.setRoles(roles);
        user.setLevel(Level.DONG);
        user.setOtpCode(otpCode);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5)); // Set thời gian hết hạn OTP là 5 phút
        userRepository.save(user);

        // Gửi OTP qua email
        emailService.sendSimpleEmail(
                user.getEmail(),
                "OTP Verification",
                "OTP sẽ hết hạn trong vòng 5p. "
                        + "Vui lòng không chia sẻ OTP này cho bất cứ ai. Mã OTP của bạn là "
                        + otpCode);

        return userMapper.toUserResponse(user);
    }

    public String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // Mã OTP gồm 6 chữ số
    }

    public void verifyEmailSignUp(UserVerifiedEmailSignUp request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_MATCH));

        if (user.getOtpCode().equalsIgnoreCase(request.getOtp())) {
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

    // OTP het han
    public void refreshOtp(UserRefreshOtp request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_MATCH));

        String otpCode = generateOTP();
        emailService.sendSimpleEmail(
                request.getEmail(),
                "OTP Verification",
                "OTP sẽ hết hạn trong vòng 5p. "
                        + "Vui lòng không chia sẻ OTP này cho bất cứ ai. Mã OTP của bạn là "
                        + otpCode);

        user.setOtpCode(otpCode);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);
    }

    // User quen mat khau
    public UserResponse forgotPassword(UserForgotPassword request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_MATCH));

        String otpCode = generateOTP();
        user.setOtpCode(otpCode);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5)); // Set thời gian hết hạn OTP là 5 phút
        userRepository.save(user);

        emailService.sendSimpleEmail(
                user.getEmail(),
                "Password Reset OTP",
                "OTP sẽ hết hạn trong vòng 5p. "
                        + "Vui lòng không chia sẻ OTP này cho bất cứ ai. Mã OTP của bạn là "
                        + otpCode);

        return userMapper.toUserResponse(user);
    }

    public void resetPassword(UserResetPassword request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_MATCH));
        if (user.getOtpCode().equalsIgnoreCase(request.getOtp())) {
            if (LocalDateTime.now().isBefore(user.getOtpExpiryTime())) {
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
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

    // User chua co tai khoan truoc do (Login with google)
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

    // FOR USER
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        UserResponse userResponse = userMapper.toUserResponse(user);
        userResponse.setNoPassword(!StringUtils.hasText(user.getPassword()));

        return userResponse;
    }

    public UserResponse updateBio(UserUpdateBio request, MultipartFile file) throws IOException {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (file != null && !file.isEmpty()) {
            String urlImage = cloudinaryService.uploadImage(file);
            user.setImage(urlImage);
        }

        userMapper.updateBio(user, request);
        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    //Cập nhật Email cho User
    public UserResponse updateEmail(UserUpdateEmail request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        user.setEmail(request.getEmail());
        user.setIsVerified(false);

        String otp = generateOTP();
        user.setOtpCode(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);

        emailService.sendSimpleEmail(
                user.getEmail(),
                "Email Update OTP",
                "OTP sẽ hết hạn trong vòng 5p. "
                        + "Vui lòng không chia sẻ OTP này cho bất cứ ai. Mã OTP của bạn là "
                        + otp);

        return userMapper.toUserResponse(user);
    }

    //Xác thực Email khi cập nhật Email cho User
    public void verifyEmailUpdate(UserVerifiedEmailUpdate request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getOtpCode().equalsIgnoreCase(request.getOtp())) {
            // Kiểm tra thời gian hết hạn OTP
            if (LocalDateTime.now().isBefore(user.getOtpExpiryTime())) {
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

    //User quên xác thực Email  -> Guest
    public UserResponse forgotVerifyEmail(ForgorVerifyEmailRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isMatch = userRepository.existsByUsernameAndEmail(request.getUsername(), request.getEmail());
        if(!isMatch){
            throw new AppException(ErrorCode.EMAIL_NOT_MATCH);
        }

        String otp = generateOTP();
        user.setOtpCode(otp);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));

        userRepository.save(user);

        emailService.sendSimpleEmail(
                user.getEmail(),
                "Email Update OTP",
                "OTP sẽ hết hạn trong vòng 5p. "
                        + "Vui lòng không chia sẻ OTP này cho bất cứ ai. Mã OTP của bạn là "
                        + otp);

        return userMapper.toUserResponse(user);
    }

    //Xác thực Email dành cho User quên xác thực Email -> Guest
    public void verifyEmailForgot(UserForgotVerifiedEmail request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_MATCH));

        if (user.getOtpCode().equalsIgnoreCase(request.getOtp())) {
            // Kiểm tra thời gian hết hạn OTP
            if (LocalDateTime.now().isBefore(user.getOtpExpiryTime())) {
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

    // User da co tai khoan truoc do
    public void updatePassword(UserUpdatePassword request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!(passwordEncoder.matches(request.getOldPassword(), user.getPassword())))
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);

        if (!(request.getNewPassword().equals(request.getCheckNewPassword()))) {
            throw new AppException(ErrorCode.PASSWORD_RE_ENTERING_INCORRECT);
        }

        user.setPassword(passwordEncoder.encode(request.getCheckNewPassword()));

        userRepository.save(user);
    }

    //Chi tieu suc khoe theo ngay
    public List<DaylyStatisticResponse> spendingHealthByDate(LocalDate date) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String userId = user.getId();

        return ordersRepository.spendingByDate(userId, date);
    }

    //Tong chi tieu suc khoe theo ngay
    public Long totalSpendingHealthByDate(LocalDate date){
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String userId = user.getId();

        return ordersRepository.totalSpendingByDate(userId, date);
    }

    //Chi tieu suc khoe theo thang
    public List<MonthlyStatisticResponse> spendingHealthByMonth(int month, int year) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String userId = user.getId();

        return ordersRepository.spendingByMonth(userId, month, year);
    }

    //Tong chi tieu suc khoe theo thang
    public Long totalSpendingHealthByMonth(int month, int year){
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String userId = user.getId();

        return ordersRepository.totalSpendingByMonth(userId, month, year);
    }

    //Chi tieu suc khoe theo nam
    public List<YearlyStatisticResponse> spendingHealthByYear(int year) {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String userId = user.getId();

        return ordersRepository.spendingByYear(userId, year);
    }

    //Tong chi tieu suc khoe theo nam
    public Long totalSpendingHealthByYear(int year){
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String userId = user.getId();

        return ordersRepository.totalSpendingByYear(userId, year);
    }

    // Role ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllUser(Pageable pageable) {
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

    @PreAuthorize("hasRole('ADMIN')")
    public void unbanUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setStatus(true);
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse createEmployee(CreateEmployeeRequest request){
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        Role role = roleRepository.findByName("EMPLOYEE")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode("12345678"))
                .roles(roles)
                .isVerified(true)
                .status(true)
                .build();

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllEmployee(Pageable pageable) {
        Role role = roleRepository.findByName("EMPLOYEE")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        return userRepository.findAllByRoles(pageable, role)
                .map(userMapper::toUserResponse);
    }
}
