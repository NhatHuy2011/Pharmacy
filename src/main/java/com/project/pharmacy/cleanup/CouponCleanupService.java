package com.project.pharmacy.cleanup;

import com.project.pharmacy.repository.CouponRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CouponCleanupService {
    CouponRepository couponRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpExpiredCoupons() {
        int deletedCoupons = couponRepository.deleteByExpireDateBefore(LocalDate.now());
        System.out.println("Deleted " + deletedCoupons + " expired coupons");
    }
}
