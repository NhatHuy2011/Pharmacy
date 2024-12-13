package com.project.pharmacy.repository;

import com.project.pharmacy.utils.TopCompany;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.pharmacy.entity.Company;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {
    boolean existsByName(String name);

    @Query(value = "SELECT c.id AS id, c.name AS name, c.image AS image, SUM(o.quantity) AS totalQuantity " +
            "FROM company c " +
            "JOIN product p ON c.id = p.company_id " +
            "JOIN price p2 ON p2.product_id = p.id " +
            "JOIN order_item o ON o.price_id = p2.id " +
            "JOIN orders o2 ON o2.id = o.order_id " +
            "WHERE o2.status = 'SUCCESS' " +
            "GROUP BY c.id, c.name, c.image " +
            "ORDER BY totalQuantity DESC ", nativeQuery = true)
    List<TopCompany> getTop20Company(Pageable pageable);

    @Query(value = "Select count(c.id) as totalCompany " +
            "From company c", nativeQuery = true)
    int getTotalCompany();
}
