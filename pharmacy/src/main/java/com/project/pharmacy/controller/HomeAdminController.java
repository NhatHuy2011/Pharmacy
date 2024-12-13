package com.project.pharmacy.controller;

import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.service.HomeAdminService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/home/admin")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomeAdminController {
    HomeAdminService homeAdminService;

    @GetMapping("/totalUser")
    public ApiResponse<Integer> getTotalUser(){
        return ApiResponse.<Integer>builder()
                .result(homeAdminService.getTotalUser())
                .build();
    }

    @GetMapping("/totalCompany")
    public ApiResponse<Integer> getTotalCompany(){
        return ApiResponse.<Integer>builder()
                .result(homeAdminService.getTotalCompany())
                .build();
    }

    @GetMapping("/totalCategory")
    public ApiResponse<Integer> getTotalCategory(){
        return ApiResponse.<Integer>builder()
                .result(homeAdminService.getTotalCategory())
                .build();
    }

    @GetMapping("/revenue/date")
    public ApiResponse<Long> getRevenueByDate(@RequestParam("date") LocalDate date){
        return ApiResponse.<Long>builder()
                .result(homeAdminService.getRevenueByDate(date))
                .build();
    }

    @GetMapping("/revenue/month")
    public ApiResponse<Long> getRevenueByMonth(@RequestParam("month") int month,
                                               @RequestParam("year") int year){
        return ApiResponse.<Long>builder()
                .result(homeAdminService.getRevenueByMonth(month, year))
                .build();
    }

    @GetMapping("/revenue/year")
    public ApiResponse<Long> getRevenueByMonth(@RequestParam("year") int year){
        return ApiResponse.<Long>builder()
                .result(homeAdminService.getRevenueByYear(year))
                .build();
    }

    @GetMapping("/revenue/beetween")
    public ApiResponse<Long> getRevenueBeetWeen(@RequestParam("startDate") LocalDate startDate,
                                                @RequestParam("endDate") LocalDate endDate){
        return ApiResponse.<Long>builder()
                .result(homeAdminService.getRevenueBeetWeenDates(startDate, endDate))
                .build();
    }
}
