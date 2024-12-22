package com.project.pharmacy.repository;

import com.project.pharmacy.enums.OrderStatus;
import com.project.pharmacy.enums.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.pharmacy.entity.Orders;

import java.time.LocalDate;

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {
    Page<Orders> findByStatus(OrderStatus status, Pageable pageable);

    Page<Orders> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);

    @Query("Select Sum(o.totalPrice) " +
            "From Orders o " +
            "Where o.status = 'SUCCESS' " +
            "And DATE(o.orderDate) = :date")
    Long revenueByDate(@Param("date") LocalDate date);

    @Query("Select Sum(o.totalPrice) " +
            "From Orders o " +
            "Where o.status = 'SUCCESS' " +
            "And MONTH(o.orderDate) = :month AND YEAR(o.orderDate) = :year")
    Long revenueByMonth(@Param("month") int month, @Param("year") int year);

    @Query("Select Sum(o.totalPrice) " +
            "From Orders o " +
            "Where o.status = 'SUCCESS' " +
            "AND YEAR(o.orderDate) = :year")
    Long revenueByYear(@Param("year") int year);

    @Query("Select Sum(o.totalPrice) " +
            "From Orders o " +
            "Where o.status = 'SUCCESS' " +
            "AND DATE(o.orderDate) BETWEEN :startDate AND :endDate")
    Long revenueBetweenDates(@Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate);

    @Query("Select Count(o.id) " +
            "From Orders o " +
            "Where o.isConfirm = false ")
    Long totalOrderNotConfirm();
}
