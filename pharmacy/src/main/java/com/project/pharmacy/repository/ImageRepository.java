package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {
    Image findFirstByProductId(String productId);
    void deleteAllByProductId(String productId);
    List<Image> findByProductId(String productId);
}
