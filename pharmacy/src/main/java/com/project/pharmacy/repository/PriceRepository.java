package com.project.pharmacy.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.pharmacy.entity.Price;
import com.project.pharmacy.entity.Product;
import com.project.pharmacy.entity.Unit;

@Repository
public interface PriceRepository extends JpaRepository<Price, String> {
    boolean existsByProductAndUnit(Product product, Unit unit);

    Optional<Set<Price>> findByProductId(String productId);

    Optional<Price> findByProductAndUnit(Product product, Unit unit);

    void deleteAllByProductId(String id);

    void deleteAllByUnitId(String id);
}
