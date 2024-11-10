package com.project.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.pharmacy.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {}
