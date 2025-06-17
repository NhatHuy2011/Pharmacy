package com.project.pharmacy.entity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false)
    Unit unit;

    @Column(nullable = false)
    Integer price;

    @Column
    Integer quantity;

    String description;

    @OneToMany(mappedBy = "price", cascade = CascadeType.REMOVE, orphanRemoval = true)
    Set<CartItem> cartItems;

    @OneToMany(mappedBy = "price", cascade = CascadeType.REMOVE, orphanRemoval = true)
    Set<OrderItem> orderItems;
}
