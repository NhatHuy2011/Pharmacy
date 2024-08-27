package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitRepository extends JpaRepository<Unit, String> {
    boolean existsByName(String name);
}
