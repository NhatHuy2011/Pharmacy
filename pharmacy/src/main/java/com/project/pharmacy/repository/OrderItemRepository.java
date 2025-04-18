package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.pharmacy.entity.OrderItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    Optional<List<OrderItem>> findByOrders(Orders orders);

    void deleteAllByOrders(Orders orders);
}
