package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Coupon;
import com.project.pharmacy.enums.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {
    List<Coupon> findAllByLevelUser(Level levelUser);
}
