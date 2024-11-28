package com.project.pharmacy.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.pharmacy.entity.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, String> {
    boolean existsByName(String name);

    void deleteAllByCategoryId(String categoryId);

    void deleteAllByCompanyId(String companyId);

    List<Product> findByCategoryId(String categoryId);

    List<Product> findByCompanyId(String companyId);

    @Query(value = "Select p from Product p JOIN Price pr On p.id = pr.product.id Where p.category.id IN :categoryIds ORDER BY pr.price ASC")
    Page<Product> findByCategoryIdsAsc(Pageable pageable, List<String> categoryIds);

    @Query(value = "Select p from Product p JOIN Price pr On p.id = pr.product.id Where p.category.id IN :categoryIds ORDER BY pr.price DESC")
    Page<Product> findByCategoryIdsDesc(Pageable pageable, List<String> categoryIds);
}
