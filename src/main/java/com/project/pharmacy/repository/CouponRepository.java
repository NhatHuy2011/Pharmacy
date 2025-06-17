package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Coupon;
import com.project.pharmacy.enums.Level;
import com.project.pharmacy.enums.CouponType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {
    List<Coupon> findAllByLevelUserAndCouponType(Level levelUser, CouponType couponType);

    List<Coupon> findAllByCouponType(CouponType couponType);

    @Modifying
    @Transactional
    @Query("DELETE FROM Coupon c WHERE c.expireDate < :expireDate")
    int deleteByExpireDateBefore(LocalDate expireDate);
}
