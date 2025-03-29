package com.project.pharmacy.mapper;

import com.project.pharmacy.dto.request.CreateCouponRequest;
import com.project.pharmacy.dto.request.UpdateCouponRequest;
import com.project.pharmacy.dto.response.CouponResponse;
import com.project.pharmacy.entity.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CouponMapper {
    Coupon toCoupon(CreateCouponRequest request);

    CouponResponse toCouponResponse(Coupon coupon);

    @Mapping(target = "createDate", ignore = true)
    void updateCoupon(@MappingTarget Coupon coupon, UpdateCouponRequest request);
}
