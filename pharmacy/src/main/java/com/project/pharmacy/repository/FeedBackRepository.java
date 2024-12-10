package com.project.pharmacy.repository;

import com.project.pharmacy.entity.FeedBack;
import com.project.pharmacy.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedBackRepository extends JpaRepository<FeedBack, String> {
    Optional<List<FeedBack>> findAllByProductAndParent(Product product, FeedBack parent);

    List<FeedBack> findByParent(FeedBack feedBack);
}
