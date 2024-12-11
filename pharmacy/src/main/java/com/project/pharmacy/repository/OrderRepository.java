package com.project.pharmacy.repository;

import com.project.pharmacy.entity.Address;
import com.project.pharmacy.entity.User;
import com.project.pharmacy.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.pharmacy.entity.Orders;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {
    List<Orders> findAllByAddress(Address address);

    Page<Orders> findByStatus(OrderStatus status, Pageable pageable);
}
