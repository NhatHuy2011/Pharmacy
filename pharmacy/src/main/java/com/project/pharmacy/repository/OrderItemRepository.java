package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.pharmacy.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    void deleteAllByOrders(Orders orders);
}
