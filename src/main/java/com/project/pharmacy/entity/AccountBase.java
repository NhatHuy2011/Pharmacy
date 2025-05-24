package com.project.pharmacy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class AccountBase {
    @Column
    String username;

    @Column
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
    String phoneNumber;

    @Column
    String image;

    @Column
    Boolean status;

    @ManyToOne
    @JoinColumn(name = "role_id")
    Role role;
}
