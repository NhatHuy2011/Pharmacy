package com.project.pharmacy.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.pharmacy.entity.InvalidatedToken;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {

    // Sử dụng @Modifying để thực hiện câu lệnh xóa
    @Modifying
    @Transactional
    @Query("DELETE FROM InvalidatedToken t WHERE t.expiryTime < :expiryTime")
    int deleteByExpiryTimeBefore(Date expiryTime);
}
