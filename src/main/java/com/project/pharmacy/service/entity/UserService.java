package com.project.pharmacy.service.entity;

import com.project.pharmacy.dto.request.auth.ForgotPasswordRequest;
import com.project.pharmacy.dto.request.auth.ForgotVerifyEmailRequest;
import com.project.pharmacy.dto.request.auth.RefreshOTP;
import com.project.pharmacy.dto.request.auth.SignUpRequest;
import com.project.pharmacy.dto.request.oauth.PasswordCreateRequest;
import com.project.pharmacy.dto.request.user.*;
import com.project.pharmacy.dto.response.entity.UserResponse;
import com.project.pharmacy.dto.response.statistic.DaylyStatisticResponse;
import com.project.pharmacy.dto.response.statistic.MonthlyStatisticResponse;
import com.project.pharmacy.dto.response.statistic.YearlyStatisticResponse;
import com.project.pharmacy.entity.Coupon;
import com.project.pharmacy.entity.Role;
import com.project.pharmacy.entity.User;
import com.project.pharmacy.enums.CouponType;
import com.project.pharmacy.enums.Level;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.UserMapper;
import com.project.pharmacy.repository.CouponRepository;
import com.project.pharmacy.repository.OrderRepository;
import com.project.pharmacy.repository.RoleRepository;
import com.project.pharmacy.repository.UserRepository;
import com.project.pharmacy.service.cloudinary.CloudinaryService;
import com.project.pharmacy.service.email.EmailService;
import com.project.pharmacy.utils.CustomMultipartFile;
import com.project.pharmacy.utils.EmailTemplateUtils;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

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

    CouponRepository couponRepository;

    // For GUEST
    public UserResponse createUser(SignUpRequest request) throws MessagingException {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        if (userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCode.EMAIL_EXISTED);

        if (!request.getPassword().equals(request.getConfirmPassword()))
            throw new AppException(ErrorCode.PASSWORD_RE_ENTERING_INCORRECT);

        if(userRepository.existsByPhoneNumber(request.getPhoneNumber())){
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        String otpCode = generateOTP();

        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setDob(request.getDob());
        user.setSex(request.getSex());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getConfirmPassword()));
        user.setStatus(false);
        user.setIsVerified(false);
        user.setRole(role);
        user.setLevel(Level.DONG);
        user.setOtpCode(otpCode);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5)); // Set thời gian hết hạn OTP là 5 phút
        userRepository.save(user);

        String html = EmailTemplateUtils.buildOtpEmail(otpCode);
        emailService.sendHtmlEmail(
                user.getEmail(),
                "PHARMACY VERIFY EMAIL",
                html
        );

        return userMapper.toUserResponse(user);
    }

    public String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // Mã OTP gồm 6 chữ số
    }

    public void sendEmailWhenPriceOverQuantity(String email) {
        try {
            // 1. Load ảnh từ resources
            ClassPathResource imgFile = new ClassPathResource("coupon.png");
            byte[] bytes = imgFile.getInputStream().readAllBytes();

            // 2. Tạo MultipartFile thủ công
            MultipartFile multipartFile = new CustomMultipartFile(
                    bytes,
                    "coupon",
                    "coupon.png",
                    "image/png"
            );

            // 3. Upload lên Cloudinary
            String url = cloudinaryService.uploadImage(multipartFile);

            // 4. Tạo coupon
            Coupon coupon = new Coupon();
            coupon.setName("Xin lỗi vì sự cố đơn hàng");
            coupon.setPercent(10);
            coupon.setMax(30000);
            coupon.setOrderRequire(100000);
            coupon.setImage(url);
            coupon.setCreateDate(LocalDate.now());
            coupon.setExpireDate(LocalDate.now().plusDays(7));
            coupon.setCouponType(CouponType.OTHER);
            coupon.setLevelUser(Level.DONG);
            coupon.setDescription("Mã giảm giá xin lỗi từ PHARMACY");

            couponRepository.save(coupon);

            String html = EmailTemplateUtils.buildOutOfStockEmail(coupon.getId());
            emailService.sendHtmlEmail(
                    email,
                    "PHARMACY SORRY",
                    html
            );

        } catch (IOException e) {
            throw new RuntimeException("Không thể upload ảnh coupon", e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
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
    public void refreshOtp(RefreshOTP request) throws MessagingException {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_MATCH));

        String otpCode = generateOTP();

        String html = EmailTemplateUtils.buildOtpEmail(otpCode);
        emailService.sendHtmlEmail(
                user.getEmail(),
                "PHARMACY VERIFY EMAIL",
                html
        );

        user.setOtpCode(otpCode);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);
    }

    // User quen mat khau
    public UserResponse forgotPassword(ForgotPasswordRequest request) throws MessagingException {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_MATCH));

        String otpCode = generateOTP();
        user.setOtpCode(otpCode);
        user.setOtpExpiryTime(LocalDateTime.now().plusMinutes(5)); // Set thời gian hết hạn OTP là 5 phút
        userRepository.save(user);

         String html = EmailTemplateUtils.buildOtpEmail(otpCode);
            emailService.sendHtmlEmail(
                    user.getEmail(),
                    "PHARMACY VERIFY EMAIL",
                    html
            );

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

        if (!request.getPhoneNumber().equals(user.getPhoneNumber()) &&
            userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.PHONE_EXISTED);
        }

        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setDob(request.getDob());
        user.setSex(request.getSex());
        user.setPhoneNumber(request.getPhoneNumber());

        if (file != null && !file.isEmpty()) {
            String urlImage = cloudinaryService.uploadImage(file);
            user.setImage(urlImage);
        }

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    //Cập nhật Email cho User
    public UserResponse updateEmail(UserUpdateEmail request) throws MessagingException {
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

        String html = EmailTemplateUtils.buildOtpEmail(otp);
        emailService.sendHtmlEmail(
                user.getEmail(),
                "PHARMACY VERIFY EMAIL",
                html
        );

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
    public UserResponse forgotVerifyEmail(ForgotVerifyEmailRequest request) throws MessagingException {
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

        String html = EmailTemplateUtils.buildOtpEmail(otp);
        emailService.sendHtmlEmail(
                user.getEmail(),
                "PHARMACY VERIFY EMAIL",
                html
        );

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

    //For ADMIN
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

    //For NURSE
    @PreAuthorize("hasRole('NURSE')")
    public UserResponse getInfoUserByPhone(String phone){
        User user = userRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .phoneNumber(user.getPhoneNumber())
                .build();

        return userResponse;
    }
}
