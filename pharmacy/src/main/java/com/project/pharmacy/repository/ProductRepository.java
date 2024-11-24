package com.project.pharmacy.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.pharmacy.entity.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
    boolean existsByName(String name);

    void deleteAllByCategoryId(String categoryId);

    void deleteAllByCompanyId(String companyId);

    List<Product> findByCategoryId(String categoryId);

    List<Product> findByCompanyId(String companyId);

    Page<Product> findByCategoryId(Pageable pageable, String id);
}
