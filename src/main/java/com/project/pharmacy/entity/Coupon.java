package com.project.pharmacy.entity;

import com.project.pharmacy.enums.Level;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column
    String name;

    @Column
    String image;

    @Column
    int percent;

    @Column
    int max;

    @Column
    int orderRequire;

    @Enumerated(EnumType.STRING)
    @Column(name = "levelUser", columnDefinition = "ENUM('DONG', 'BAC', 'VANG', 'BACHKIM', 'KIMCUONG') DEFAULT 'DONG'")
    Level levelUser;

    @Column
    String description;

    @Column
    LocalDate createDate;

    @Column
    LocalDate expireDate;
}
