package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Product;
import com.project.pharmacy.entity.Price;
import com.project.pharmacy.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface PriceRepository extends JpaRepository<Price, String> {
    boolean existsByProductAndUnit(Product product, Unit unit);

    Set<Price> findByProductId(String productId);

    Price findByProductAndUnit(Product product, Unit unit);

    void deleteAllByProductId(String id);

    void deleteAllByUnitId(String id);
}
