package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Cart;
import com.project.pharmacy.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, String> {
    List<CartItem> findAllByCart(Cart cart);
}
