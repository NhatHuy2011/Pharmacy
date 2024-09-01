package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    boolean existsByName(String name);

    void deleteAllByCategoryId(String categoryId);

    void deleteAllByCompanyId(String companyId);


    List<Product> findByCategoryId(String categoryId);

    List<Product> findByCompanyId(String companyId);
}
