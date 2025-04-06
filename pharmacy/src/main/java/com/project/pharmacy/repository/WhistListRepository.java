package com.project.pharmacy.repository;

import com.project.pharmacy.entity.User;
import com.project.pharmacy.entity.WhistList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WhistListRepository extends JpaRepository<WhistList, String> {
    List<WhistList> findByUser(User user);
}
