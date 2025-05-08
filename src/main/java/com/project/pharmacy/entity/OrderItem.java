package com.project.pharmacy.entity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @EqualsAndHashCode.Exclude
    Orders orders;

    @ManyToOne
    @JoinColumn(name = "price_id")
    Price price;

    @Column
    int quantity;

    @Column
    int amount;

    @Column
    String image;
}
