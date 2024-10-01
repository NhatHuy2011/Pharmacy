package com.project.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.pharmacy.entity.Unit;

@Repository
public interface UnitRepository extends JpaRepository<Unit, String> {
    boolean existsByName(String name);
}
