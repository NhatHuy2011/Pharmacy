package com.project.pharmacy.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

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

    String password;

    @Column
    String firstname;

    @Column
    String lastname;

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

    @Column
    Boolean status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    Set<Role> roles;

    @Column
    String otpCode;

    @Column
    LocalDateTime otpExpiryTime;

    @Column
    Boolean isVerified;
}
