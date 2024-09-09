package com.project.pharmacy.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false)
    String username;

    @Column(nullable = false)
    String password;

    @Column
    String fullname;

    @Column
    LocalDate dob;

    @Column
    String sex;

    @Column
    Integer phone_number;

    @Column
    String email;

    @Column
    String image;

    @Column
    int point;
}
