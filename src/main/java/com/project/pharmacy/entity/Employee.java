package com.project.pharmacy.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Employee extends AccountBase{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(columnDefinition = "LONGTEXT")
    String specilization;

    @Column(columnDefinition = "LONGTEXT")
    String description;

    @Column(columnDefinition = "LONGTEXT")
    String workExperience;

    @Column(columnDefinition = "LONGTEXT")
    String education;

    @Column
    int workTime;

    @Column
    int salary;
}
