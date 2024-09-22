package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    boolean existsByName(String name);
    Optional<List<Permission>> findAllByNameIn(Set<String> name);
}
