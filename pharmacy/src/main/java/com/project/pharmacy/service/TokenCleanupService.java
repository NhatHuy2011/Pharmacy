package com.project.pharmacy.service;

import com.project.pharmacy.repository.InvalidatedTokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenCleanupService {

    private final InvalidatedTokenRepository invalidatedTokenRepository;

    public TokenCleanupService(InvalidatedTokenRepository invalidatedTokenRepository) {
        this.invalidatedTokenRepository = invalidatedTokenRepository;
    }

    // Chạy hàng ngày lúc 00:00 để xóa token đã hết hạn
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpExpiredTokens() {
        Date now = new Date(); // Lấy thời gian hiện tại
        int deletedTokens = invalidatedTokenRepository.deleteByExpiryTimeBefore(now);
        System.out.println("Deleted " + deletedTokens + " expired tokens");
    }
}
