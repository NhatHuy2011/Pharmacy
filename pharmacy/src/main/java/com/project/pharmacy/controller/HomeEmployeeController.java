package com.project.pharmacy.controller;

import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.service.HomeEmployeeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home/employee")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomeEmployeeController {
    HomeEmployeeService homeEmployeeService;

    @GetMapping("/totalProduct")
    public ApiResponse<Integer> getTotalProduct(){
        return ApiResponse.<Integer>builder()
                .result(homeEmployeeService.getTotalProduct())
                .build();
    }

    @GetMapping("/totalOrderNotConfirm")
    public ApiResponse<Long> getTotalOrderNotConfirm(){
        return ApiResponse.<Long>builder()
                .result(homeEmployeeService.getTotalOrderNotConfirm())
                .build();
    }
}
