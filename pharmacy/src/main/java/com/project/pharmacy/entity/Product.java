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
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @Column(nullable = false)
    String benefits;

    @Column(nullable = false)
    String ingredients;

    @Column(nullable = false)
    String constraindication;

    @Column(nullable = false)
    String object_use;

    @Column(nullable = false)
    String instruction;

    @Column(nullable = false)
    String preserve;

    String description;

    String note;

    @Column(nullable = false)
    boolean doctor_advice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    Company company;

    @OneToMany(mappedBy = "product")
    Set<Image> images;

    @OneToMany(mappedBy = "product")
    Set<ProductUnit> productUnits;
}
