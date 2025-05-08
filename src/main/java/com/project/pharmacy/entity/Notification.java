package com.project.pharmacy.entity;

import com.project.pharmacy.enums.Level;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column
    String title;

    @Column(columnDefinition = "LONGTEXT")
    String content;

    @Column
    String image;

    @Column
    LocalDate createDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", columnDefinition = "ENUM('DONG', 'BAC', 'VANG', 'BACHKIM', 'KIMCUONG') DEFAULT 'DONG'")
    Level level;
}
