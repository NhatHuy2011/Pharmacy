package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    boolean existsByName(String name);

    @Modifying
    @Query("UPDATE Product p SET p.unit = null WHERE p.unit.id = :unitId")
    void updateUnitIdToNull(String unitId);

    @Modifying
    @Query("UPDATE Product p SET p.company = null WHERE p.company.id = :companyId")
    void updateCompanyIdToNull(String companyId);

    @Modifying
    @Query("UPDATE Product p SET p.category = null WHERE p.category.id = :categoryId")
    void updateCategoryIdToNull(String categoryId);
}
