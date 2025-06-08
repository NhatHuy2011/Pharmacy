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
public class User extends AccountBase{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column
    String email;

    @OneToMany(mappedBy = "user")
    List<Address> addresses;

    @Column
    int point;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", columnDefinition = "ENUM('DONG', 'BAC', 'VANG', 'BACHKIM', 'KIMCUONG') DEFAULT 'DONG'")
    Level level;

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
    @EqualsAndHashCode.Exclude
    List<FeedBack> feedBacks;

    @OneToMany(mappedBy = "user")
    List<WhistList> whistLists;

    @ManyToMany
    List<Notification> notifications;
}
