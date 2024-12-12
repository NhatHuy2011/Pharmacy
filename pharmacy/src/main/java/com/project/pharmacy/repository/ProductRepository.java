package com.project.pharmacy.repository;

import java.util.List;

import com.project.pharmacy.utils.ProductBestSeller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "Select p from Product p " +
            "JOIN Price pr On p.id = pr.product.id " +
            "Where p.category.id IN :categoryIds " +
            "ORDER BY pr.price ASC")
    Page<Product> findByCategoryIdsAsc(Pageable pageable, List<String> categoryIds);

    @Query(value = "Select p from Product p " +
            "JOIN Price pr On p.id = pr.product.id " +
            "Where p.category.id IN :categoryIds " +
            "ORDER BY pr.price DESC")
    Page<Product> findByCategoryIdsDesc(Pageable pageable, List<String> categoryIds);


    @Query(value = "Select p from Product p " +
            "ORDER BY p.dateCreation DESC")
    List<Product> findTop20NewProduct(Pageable pageable);

    @Query(value = "SELECT p1.id AS productId, SUM(o.quantity) AS totalQuantity " +
            "FROM product p1 " +
            "JOIN price p2 ON p1.id = p2.product_id " +
            "JOIN order_item o ON p2.id = o.price_id " +
            "JOIN orders o2 ON o.order_id = o2.id " +
            "WHERE o2.status = 'SUCCESS' " +
            "GROUP BY p1.id " +
            "ORDER BY totalQuantity DESC " , nativeQuery = true)
    List<ProductBestSeller> findTop20ProductBestSeller(Pageable pageable);

    @Query("Select p From Product p Where p.id IN :ids")
    List<Product> findProductsByIds(@Param("ids") List<String> ids);
}
