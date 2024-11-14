package com.project.pharmacy.controller;

import com.project.pharmacy.dto.request.CreateOrderRequestAtCartGuest;
import com.project.pharmacy.dto.request.CreateOrderRequestAtCartUser;
import com.project.pharmacy.dto.request.CreateOrderRequestAtHomeGuest;
import com.project.pharmacy.dto.request.CreateOrderRequestAtHomeUser;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.OrderResponse;
import com.project.pharmacy.service.OrderService;
import com.project.pharmacy.utils.OrderTemporary;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    @PostMapping("/cart")
    public ApiResponse<OrderResponse> createOrderAtCartUser(@RequestBody @Valid CreateOrderRequestAtCartUser request){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrderAtCartUser(request))
                .build();
    }

    @PostMapping("/home")
    public ApiResponse<OrderResponse> createOrderAtHomeUser(@RequestBody @Valid CreateOrderRequestAtHomeUser request){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrderAtHomeUser(request))
                .build();
    }

    @PostMapping("/guest/cart")
    public ApiResponse<OrderTemporary> createOrderAtCartGuest(@RequestBody @Valid CreateOrderRequestAtCartGuest request, HttpSession session){
        return ApiResponse.<OrderTemporary>builder()
                .result(orderService.createOrderAtCartGuest(request, session))
                .build();
    }

    @PostMapping("/guest/home")
    public ApiResponse<OrderTemporary> createOrderAtHomeGuest(@RequestBody @Valid CreateOrderRequestAtHomeGuest request, HttpSession session){
        return ApiResponse.<OrderTemporary>builder()
                .result(orderService.createOrderAtHomeGuest(request, session))
                .build();
    }
}

