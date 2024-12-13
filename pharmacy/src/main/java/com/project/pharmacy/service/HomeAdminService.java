package com.project.pharmacy.service;

import com.project.pharmacy.repository.CategoryRepository;
import com.project.pharmacy.repository.CompanyRepository;
import com.project.pharmacy.repository.OrderRepository;
import com.project.pharmacy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomeAdminService {
    UserRepository userRepository;

    CompanyRepository companyRepository;

    CategoryRepository categoryRepository;

    OrderRepository orderRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public int getTotalUser(){
        return userRepository.getTotalUser();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public int getTotalCompany(){
        return companyRepository.getTotalCompany();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public int getTotalCategory(){
        return categoryRepository.getTotalCategory();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Long getRevenueByDate(LocalDate date){
        return orderRepository.revenueByDate(date);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Long getRevenueByMonth(int month, int year){
        return orderRepository.revenueByMonth(month, year);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Long getRevenueByYear(int year){
        return orderRepository.revenueByYear(year);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Long getRevenueBeetWeenDates(LocalDate startDate, LocalDate endDate){
        return orderRepository.revenueBetweenDates(startDate, endDate);
    }
}
