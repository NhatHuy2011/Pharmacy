package com.project.pharmacy.controller.entity;

import com.project.pharmacy.dto.request.coupon.CreateCouponRequest;
import com.project.pharmacy.dto.request.coupon.UpdateCouponRequest;
import com.project.pharmacy.dto.response.common.ApiResponse;
import com.project.pharmacy.dto.response.entity.CouponResponse;
import com.project.pharmacy.enums.CouponType;
import com.project.pharmacy.service.entity.CouponService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/coupon")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CouponController {
    CouponService couponService;

    @PostMapping
    public ApiResponse<CouponResponse> createCoupon(@RequestPart("createCoupon") @Valid CreateCouponRequest request,
                                   @RequestPart("file") MultipartFile file) throws IOException {
        return ApiResponse.<CouponResponse>builder()
                .result(couponService.createCoupon(request, file))
                .build();
    }

    @PutMapping
    public ApiResponse<CouponResponse> updateCoupon(@RequestPart("updateCoupon") UpdateCouponRequest request,
                                                    @RequestPart("file") MultipartFile file) throws IOException {
        return ApiResponse.<CouponResponse>builder()
                .result(couponService.updateCoupon(request, file))
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<Void> deleteCoupon(@PathVariable String id){
        couponService.deleteCoupon(id);
        return ApiResponse.<Void>builder()
                .message("Delete coupon successful")
                .build();
    }

    @GetMapping
    public ApiResponse<List<CouponResponse>> getAll(){
        return ApiResponse.<List<CouponResponse>>builder()
                .result(couponService.getAllCoupon())
                .build();
    }

    @GetMapping("/type")
    public ApiResponse<List<CouponResponse>> getAllByType(@RequestParam CouponType couponType){
        return ApiResponse.<List<CouponResponse>>builder()
                .result(couponService.getAllCouponByType(couponType))
                .build();
    }

    @GetMapping("/user")
    public ApiResponse<List<CouponResponse>> getCouponByLevelUser(){
        return ApiResponse.<List<CouponResponse>>builder()
                .result(couponService.getCouponByLevelUser())
                .build();
    }

    @GetMapping("/user/id")
    public ApiResponse<CouponResponse> getCouponById(@RequestParam String couponId){
        return ApiResponse.<CouponResponse>builder()
                .result(couponService.getCouponById(couponId))
                .build();
    }
}
