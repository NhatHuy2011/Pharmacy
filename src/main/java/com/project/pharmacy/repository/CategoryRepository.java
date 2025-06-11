package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    boolean existsByName(String name);

    List<Category> findByParent(Category parent);

    @Query(value = "Select c From Category c Where c.parent.id = :parent")
    List<Category> findAllByParentId(String parent);

    @Query(value = "Select count(c.id) as totalCategory " +
            "From category c", nativeQuery = true)
    int getTotalCategory();
}
