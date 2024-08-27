package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
    boolean existsByName(String name);
}
