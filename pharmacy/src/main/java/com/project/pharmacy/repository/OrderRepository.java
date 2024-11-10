package com.project.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.pharmacy.entity.Orders;

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {}
