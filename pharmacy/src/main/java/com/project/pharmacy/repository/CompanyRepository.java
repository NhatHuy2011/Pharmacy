package com.project.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.pharmacy.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
    boolean existsByName(String name);
}
