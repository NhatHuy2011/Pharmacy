package com.project.pharmacy.service;

import com.project.pharmacy.dto.response.DaylyStatisticResponse;
import com.project.pharmacy.dto.response.MonthlyStatisticResponse;
import com.project.pharmacy.dto.response.YearlyStatisticResponse;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomeAdminService {
    UserRepository userRepository;

    CompanyRepository companyRepository;

    CategoryRepository categoryRepository;

    OrderRepository orderRepository;

    //Tong user
    @PreAuthorize("hasRole('ADMIN')")
    public int getTotalUser(){
        return userRepository.getTotalUser();
    }

    //Tong cong ty
    @PreAuthorize("hasRole('ADMIN')")
    public int getTotalCompany(){
        return companyRepository.getTotalCompany();
    }

    //Tong doanh muc
    @PreAuthorize("hasRole('ADMIN')")
    public int getTotalCategory(){
        return categoryRepository.getTotalCategory();
    }

    //Tong doanh thu theo ngay
    @PreAuthorize("hasRole('ADMIN')")
    public Long getTotalRevenueByDate(LocalDate date){
        return orderRepository.totalRevenueByDate(date);
    }

    //Doanh thu theo thang
    @PreAuthorize("hasRole('ADMIN')")
    public List<MonthlyStatisticResponse> getRevenueByMonth(int month, int year){
        return orderRepository.revenueByMonth(month, year);
    }

    //Tong doanh thu theo thang
    @PreAuthorize("hasRole('ADMIN')")
    public Long getTotalRevenueByMonth(int month, int year){
        return orderRepository.totalRevenueByMonth(month, year);
    }

    //Doanh thu theo nam
    @PreAuthorize("hasRole('ADMIN')")
    public List<YearlyStatisticResponse> getRevenueByYear(int year){
        return orderRepository.revenueByYear(year);
    }

    //Tong doanh thu theo nam
    @PreAuthorize("hasRole('ADMIN')")
    public Long getTotalRevenueByYear(int year){
        return orderRepository.totalRevenueByYear(year);
    }
}
