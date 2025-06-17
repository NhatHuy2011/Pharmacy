package com.project.pharmacy.service.entity;

import com.project.pharmacy.dto.request.coupon.CreateCouponRequest;
import com.project.pharmacy.dto.request.notification.CreateNotificationRequest;
import com.project.pharmacy.dto.request.coupon.UpdateCouponRequest;
import com.project.pharmacy.dto.response.entity.CouponResponse;
import com.project.pharmacy.entity.Coupon;
import com.project.pharmacy.entity.Notification;
import com.project.pharmacy.entity.User;
import com.project.pharmacy.enums.CouponType;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.CouponMapper;
import com.project.pharmacy.mapper.NotificationMapper;
import com.project.pharmacy.repository.CouponRepository;
import com.project.pharmacy.repository.NotificationRepository;
import com.project.pharmacy.repository.UserRepository;
import com.project.pharmacy.service.cloudinary.CloudinaryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CouponService {
    CouponRepository couponRepository;

    UserRepository userRepository;

    CouponMapper couponMapper;

    CloudinaryService cloudinaryService;

    NotificationService notificationService;

    NotificationRepository notificationRepository;

    NotificationMapper notificationMapper;

    @PreAuthorize("hasRole('EMPLOYEE')")
    public CouponResponse createCoupon(CreateCouponRequest request, MultipartFile file) throws IOException {
        String url = cloudinaryService.uploadImage(file);

        Coupon coupon = couponMapper.toCoupon(request);
        coupon.setCouponType(request.getCouponType());
        coupon.setImage(url);
        coupon.setCreateDate(LocalDate.now());

        couponRepository.save(coupon);

        Notification notification = Notification.builder()
                .title(coupon.getName())
                .content(coupon.getDescription())
                .createDate(coupon.getCreateDate())
                .level(coupon.getLevelUser())
                .image(url)
                .build();
        notificationRepository.save(notification);

        CreateNotificationRequest notificationRequest = notificationMapper.toNotificationRequest(notification);

        notificationService.sendNotification(notificationRequest);

        return couponMapper.toCouponResponse(coupon);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    public CouponResponse updateCoupon(UpdateCouponRequest request, MultipartFile file) throws IOException {
        Coupon coupon = couponRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));

        String url = cloudinaryService.uploadImage(file);

        coupon.setImage(url);
        coupon.setCouponType(request.getCouponType());

        couponMapper.updateCoupon(coupon, request);

        couponRepository.save(coupon);

        return couponMapper.toCouponResponse(coupon);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    public void deleteCoupon(String id){
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));

        couponRepository.delete(coupon);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public List<CouponResponse> getAllCouponByType(CouponType couponType){
        return couponRepository.findAllByCouponType(couponType).stream()
                .map(couponMapper::toCouponResponse)
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public List<CouponResponse> getAllCoupon(){
        return couponRepository.findAll().stream()
                .map(couponMapper::toCouponResponse)
                .toList();
    }

    public List<CouponResponse> getCouponByLevelUser(CouponType couponType){
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<Coupon> couponByLevels = couponRepository.findAllByLevelUserAndCouponType(user.getLevel(), couponType);

        List<CouponResponse> couponResponses = couponByLevels.stream()
                .map(couponMapper::toCouponResponse)
                .toList();

        return couponResponses;
    }

    @PreAuthorize("hasRole('USER')")
    public CouponResponse getCouponById(String id){
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));

        return couponMapper.toCouponResponse(coupon);
    }
}
