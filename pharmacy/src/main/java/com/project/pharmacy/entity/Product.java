package com.project.pharmacy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    int price;

    @Column(nullable = false)
    int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    Unit unit;

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
}
