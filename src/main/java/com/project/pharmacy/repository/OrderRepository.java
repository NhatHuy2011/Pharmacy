package com.project.pharmacy.repository;

import com.project.pharmacy.dto.response.statistic.DaylyStatisticResponse;
import com.project.pharmacy.dto.response.statistic.MonthlyStatisticResponse;
import com.project.pharmacy.dto.response.statistic.YearlyStatisticResponse;
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
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, String> {
    Page<Orders> findByStatus(OrderStatus status, Pageable pageable);

    Page<Orders> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);

    //FOR ADMIN
    @Query("Select Sum(o.totalPrice) " +
            " From Orders o " +
            " Where o.status = 'SUCCESS' " +
            " AND DATE(o.orderDate) = :date ")
    Long totalRevenueByDate(@Param("date") LocalDate date);

    @Query("SELECT new com.project.pharmacy.dto.response.statistic.MonthlyStatisticResponse(DATE(o.orderDate), SUM(o.totalPrice)) " +
            " FROM Orders o " +
            " WHERE o.status = 'SUCCESS' " +
            " AND MONTH(o.orderDate) = :month AND YEAR(o.orderDate) = :year " +
            " GROUP BY DATE(o.orderDate) " +
            " ORDER BY DATE(o.orderDate) ")
    List<MonthlyStatisticResponse> revenueByMonth(@Param("month") int month, @Param("year") int year);

    @Query("Select Sum(o.totalPrice) " +
            " From Orders o " +
            " Where o.status = 'SUCCESS' " +
            " AND MONTH(o.orderDate) = :month AND YEAR(o.orderDate) = :year")
    Long totalRevenueByMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT new com.project.pharmacy.dto.response.statistic.YearlyStatisticResponse(MONTH(o.orderDate), SUM(o.totalPrice)) " +
            " FROM Orders o " +
            " WHERE o.status = 'SUCCESS' " +
            " AND YEAR(o.orderDate) = :year " +
            " GROUP BY MONTH(o.orderDate) " +
            " ORDER BY MONTH(o.orderDate)")
    List<YearlyStatisticResponse> revenueByYear(@Param("year") int year);

    @Query("Select Sum(o.totalPrice) " +
            " From Orders o " +
            " Where o.status = 'SUCCESS' " +
            " AND YEAR(o.orderDate) = :year")
    Long totalRevenueByYear(@Param("year") int year);

    //FOR ADMIN OR EMPLOYEE
    @Query("Select Count(o.id) " +
            " From Orders o " +
            " Where o.isConfirm = false ")
    Long totalOrderNotConfirm();

    //FOR USER
    @Query("SELECT o.orderDate as time, o.totalPrice as money" +
            " From Orders o " +
            " Where o.status = 'SUCCESS' " +
            " AND o.user.id = :userId " +
            " And DATE(o.orderDate) = :date " +
            " GROUP BY o.orderDate, o.totalPrice")
    List<DaylyStatisticResponse> spendingByDate(@Param("userId") String userId, @Param("date") LocalDate date);

    @Query("Select Sum(o.totalPrice) " +
            " From Orders o " +
            " Where o.status = 'SUCCESS' " +
            " AND o.user.id = :userId " +
            " AND DATE(o.orderDate) = :date")
    Long totalSpendingByDate(@Param("userId") String userId, @Param("date") LocalDate date);

    @Query("SELECT new com.project.pharmacy.dto.response.statistic.MonthlyStatisticResponse(DATE(o.orderDate), SUM(o.totalPrice)) " +
            " FROM Orders o " +
            " WHERE o.status = 'SUCCESS' " +
            " AND MONTH(o.orderDate) = :month AND YEAR(o.orderDate) = :year " +
            " AND o.user.id = :userId " +
            " GROUP BY DATE(o.orderDate) " +
            " ORDER BY DATE(o.orderDate) ")
    List<MonthlyStatisticResponse> spendingByMonth(@Param("userId") String userId, @Param("month") int month, @Param("year") int year);

    @Query("Select Sum(o.totalPrice) " +
            " From Orders o " +
            " Where o.status = 'SUCCESS' " +
            " AND o.user.id = :userId " +
            " AND MONTH(o.orderDate) = :month AND YEAR(o.orderDate) = :year ")
    Long totalSpendingByMonth(@Param("userId") String userId, @Param("month") int month, @Param("year") int year);

    @Query("SELECT new com.project.pharmacy.dto.response.statistic.YearlyStatisticResponse(MONTH(o.orderDate), SUM(o.totalPrice))" +
            " FROM Orders o " +
            " WHERE o.status = 'SUCCESS' " +
            " AND o.user.id = :userId " +
            " AND YEAR(o.orderDate) = :year " +
            " GROUP BY MONTH(o.orderDate) " +
            " ORDER BY MONTH(o.orderDate)")
    List<YearlyStatisticResponse> spendingByYear(@Param("userId") String userId, @Param("year") int year);

    @Query("Select Sum(o.totalPrice) " +
            " From Orders o " +
            " Where o.status = 'SUCCESS' " +
            " AND o.user.id = :userId " +
            " AND YEAR(o.orderDate) = :year ")
    Long totalSpendingByYear(@Param("userId") String userId, @Param("year") int year);
}
