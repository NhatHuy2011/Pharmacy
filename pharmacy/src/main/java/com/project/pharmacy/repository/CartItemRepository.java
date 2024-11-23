package com.project.pharmacy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.pharmacy.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, String> {

}
