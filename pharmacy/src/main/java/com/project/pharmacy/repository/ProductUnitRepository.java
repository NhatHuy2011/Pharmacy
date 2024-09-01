package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Product;
import com.project.pharmacy.entity.ProductUnit;
import com.project.pharmacy.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductUnitRepository extends JpaRepository<ProductUnit, String> {
    boolean existsByProductAndUnit(Product product, Unit unit);

    List<ProductUnit> findByProductId(String productId);

    ProductUnit findByProductAndUnit(Product product, Unit unit);

    void deleteAllByProductId(String id);

    void deleteAllByUnitId(String id);
}
