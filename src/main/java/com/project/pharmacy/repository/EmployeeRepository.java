package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Employee;
import com.project.pharmacy.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {
    boolean existsByUsername(String username);

    Page<Employee> findAllByRole(Pageable pageable, Role role);

    Optional<Employee> findByUsername(String name);
}
