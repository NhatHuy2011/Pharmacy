package com.project.pharmacy.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.project.pharmacy.enums.OrderStatus;
import com.project.pharmacy.enums.PaymentMethod;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    User user;

    @Column
    int totalPrice;

    @ManyToOne
    @JoinColumn(name = "address_id")
    Address address;

    @OneToMany(mappedBy = "orders")
    List<OrderItem> orderItems;

    @Column
    LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('PENDING', 'SUCCESS', 'FAILED', 'CANCELLED') DEFAULT 'PENDING'")
    OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "paymentMethod", columnDefinition = "ENUM('CASH', 'VNPAY', 'MOMO', 'ZALOPAY') DEFAULT 'CASH'")
    PaymentMethod paymentMethod;
}
