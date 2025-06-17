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

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    String benefits;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    String ingredients;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    String constraindication;

    @Column(nullable = false)
    String object_use;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    String instruction;

    @Column(nullable = false)
    String preserve;

    @Column
    String description;

    @Column(columnDefinition = "LONGTEXT")
    String note;

    @Column
    LocalDate dateCreation;

    @Column
    LocalDate dateExpiration;

    @Column(nullable = false)
    Boolean doctor_advice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    Company company;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Image> images;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Price> prices;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<FeedBack> feedBacks;

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<WhistList> whistLists;
}
