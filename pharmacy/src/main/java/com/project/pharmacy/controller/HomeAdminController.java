package com.project.pharmacy.controller;

import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.DaylyStatisticResponse;
import com.project.pharmacy.dto.response.MonthlyStatisticResponse;
import com.project.pharmacy.dto.response.YearlyStatisticResponse;
import com.project.pharmacy.service.HomeAdminService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

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

    @GetMapping("/revenue/total/date")
    public ApiResponse<Long> getTotalRevenueByDate(@RequestParam("date") LocalDate date){
        return ApiResponse.<Long>builder()
                .result(homeAdminService.getTotalRevenueByDate(date))
                .build();
    }

    @GetMapping("/revenue/month")
    public ApiResponse<List<MonthlyStatisticResponse>> getRevenueByMonth(@RequestParam("month") int month,
                                                                         @RequestParam("year") int year){
        return ApiResponse.<List<MonthlyStatisticResponse>>builder()
                .result(homeAdminService.getRevenueByMonth(month, year))
                .build();
    }

    @GetMapping("/revenue/total/month")
    public ApiResponse<Long> getTotalRevenueByMonth(@RequestParam("month") int month,
                        @RequestParam("year") int year){
        return ApiResponse.<Long>builder()
                .result(homeAdminService.getTotalRevenueByMonth(month, year))
                .build();
    }

    @GetMapping("/revenue/year")
    public ApiResponse<List<YearlyStatisticResponse>> getRevenueByYear(@RequestParam("year") int year){
        return ApiResponse.<List<YearlyStatisticResponse>>builder()
                .result(homeAdminService.getRevenueByYear(year))
                .build();
    }

    @GetMapping("/revenue/total/year")
    public ApiResponse<Long> getTotalRevenueByYear(@RequestParam("year") int year){
        return ApiResponse.<Long>builder()
                .result(homeAdminService.getTotalRevenueByYear(year))
                .build();
    }
}
