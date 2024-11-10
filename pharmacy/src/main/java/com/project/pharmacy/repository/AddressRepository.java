package com.project.pharmacy.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.pharmacy.entity.Address;
import com.project.pharmacy.entity.User;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    //Set<Address> findByUser(User user);
}
