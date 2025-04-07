package com.project.pharmacy.entity;

import jakarta.persistence.*;

import com.project.pharmacy.enums.AddressCategory;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.LastModifiedBy;

import java.util.List;
import java.util.Objects;

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

    @OneToMany(mappedBy = "address", cascade = CascadeType.REMOVE, orphanRemoval = true)
    List<Orders> orders;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return  phone == address.phone &&
                Objects.equals(fullname, address.fullname) &&
                Objects.equals(province, address.province) &&
                Objects.equals(district, address.district) &&
                Objects.equals(village, address.village) &&
                Objects.equals(this.address, address.address) &&
                addressCategory == address.addressCategory;
    }
}
