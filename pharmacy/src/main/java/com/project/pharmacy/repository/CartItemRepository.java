package com.project.pharmacy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.pharmacy.entity.Cart;
import com.project.pharmacy.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
}
