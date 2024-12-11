package com.project.pharmacy.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.project.pharmacy.enums.Level;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @OneToMany(mappedBy = "user")
    List<Address> addresses;

    @Column
    int point;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", columnDefinition = "ENUM('DONG', 'BAC', 'VANG', 'BACHKIM', 'KIMCUONG') DEFAULT 'DONG'")
    Level level;

    @Column
    Boolean status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role> roles;

    @Column
    String otpCode;

    @Column
    LocalDateTime otpExpiryTime;

    @Column
    Boolean isVerified;

    @OneToOne(mappedBy = "user")
    @EqualsAndHashCode.Exclude
    Cart cart;

    @OneToMany(mappedBy = "user")
    @EqualsAndHashCode.Exclude
    List<Orders> orders;

    @OneToMany(mappedBy = "user")
    List<FeedBack> feedBacks;
}
