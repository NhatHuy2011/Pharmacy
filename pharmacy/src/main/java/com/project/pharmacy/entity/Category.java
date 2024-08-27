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
