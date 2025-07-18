package com.project.pharmacy.repository;

import com.project.pharmacy.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndEmail(String username, String email);

    boolean existsByPhoneNumber(String phone);

    Optional<User> findByUsername(String username);

    Optional<User> findByPhoneNumber(String phone);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_roles WHERE role_id = :roleId", nativeQuery = true)
    void removeRoleFromUsers(@Param("roleId") String roleId);

    Optional<User> findByEmail(String email);

    @Query(value = "Select count(u.id) as TotalUser " +
            "From user u", nativeQuery = true)
    int getTotalUser();
}
