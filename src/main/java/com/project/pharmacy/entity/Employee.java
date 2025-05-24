package com.project.pharmacy.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @Column
    String specilization;

    @Column
    String description;

    @Column
    String workExperience;

    @Column
    String education;

    @Column
    int workTime;

    @Column
    int salary;
}
