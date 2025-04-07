package com.project.pharmacy.cleanup;

import java.util.Date;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.project.pharmacy.repository.InvalidatedTokenRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenCleanupService {

    InvalidatedTokenRepository invalidatedTokenRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpExpiredTokens() {
        Date now = new Date(); // Lấy thời gian hiện tại
        int deletedTokens = invalidatedTokenRepository.deleteByExpiryTimeBefore(now);
        System.out.println("Deleted " + deletedTokens + " expired tokens");
    }
}
