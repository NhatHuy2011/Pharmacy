package com.project.pharmacy.entity;

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
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String name;

    String image;
    String description;

    @ManyToOne
    @JoinColumn(name = "parent")
    Category parent;

    @OneToMany(mappedBy = "category")
    Set<Product> products;
}
