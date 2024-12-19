package com.project.pharmacy.repository;

import java.util.List;
import java.util.Optional;

import com.project.pharmacy.entity.Role;
import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.pharmacy.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndEmail(String username, String email);

    Optional<User> findByUsername(String username);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_roles WHERE role_id = :roleId", nativeQuery = true)
    void removeRoleFromUsers(@Param("roleId") String roleId);

    Optional<User> findByEmail(String email);

    Page<User> findAllByRoles(Pageable pageable, Role role);

    @Query(value = "Select count(u.id) as TotalUser " +
            "From user u", nativeQuery = true)
    int getTotalUser();
}
