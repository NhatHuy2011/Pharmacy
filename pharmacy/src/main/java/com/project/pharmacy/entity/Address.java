package com.project.pharmacy.entity;

import jakarta.persistence.*;

import com.project.pharmacy.enums.AddressCategory;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Column
    String fullname;

    @Column
    int phone;

    @Column
    String province;

    @Column
    String district;

    @Column
    String village;

    @Column
    String address;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('HOUSE', 'COMPANY') DEFAULT 'HOUSE'")
    AddressCategory addressCategory;

    @Column
    Boolean addressDefault;

    @OneToMany(mappedBy = "address")
    List<Orders> orders;
}
