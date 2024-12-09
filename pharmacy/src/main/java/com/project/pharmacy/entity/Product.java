package com.project.pharmacy.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

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
    Integer quantity;

    @Column(nullable = false)
    String benefits;

    @Column(nullable = false)
    String ingredients;

    String constraindication;

    @Column(nullable = false)
    String object_use;

    @Column(nullable = false)
    String instruction;

    @Column(nullable = false)
    String preserve;

    String description;

    String note;

    LocalDate dateCreation;

    LocalDate dateExpiration;

    @Column(nullable = false)
    Boolean doctor_advice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    Company company;

    @OneToMany(mappedBy = "product")
    List<Image> images;

    @OneToMany(mappedBy = "product")
    Set<Price> prices;

    @OneToMany(mappedBy = "product")
    List<FeedBack> feedBacks;
}
