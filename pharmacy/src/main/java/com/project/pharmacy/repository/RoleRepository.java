package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    boolean existsByName(String name);

    Optional<Role> findByName(String name);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM role_permissions WHERE permission_id = :permissionId", nativeQuery = true)
    void removePermissionFromRoles(@Param("permissionId") String permissionId);
}
