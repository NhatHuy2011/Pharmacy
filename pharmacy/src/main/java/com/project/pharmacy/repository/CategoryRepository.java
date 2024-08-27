package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    boolean existsByName(String name);
    List<Category> findByParent(Category parent);

}
