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
import org.springframework.web.bind.annotation.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderController {
    OrderService orderService;

    //For User
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

    @GetMapping("/history")
    public ApiResponse<List<OrderResponse>> getOrderByUser(){
        List<OrderResponse> orderResponses = orderService.getOrderByUser();
        if (orderResponses != null){
            return ApiResponse.<List<OrderResponse>>builder()
                    .result(orderResponses)
                    .build();
        }
        else {
            return ApiResponse.<List<OrderResponse>>builder()
                    .message("Bạn chưa có đơn hàng nào đã mua!")
                    .build();
        }
    }

    //For Guest
    @PostMapping("/guest/cart")
    public ApiResponse<OrderTemporary> createOrderAtCartGuest(@RequestBody CreateOrderRequestAtCartGuest request, HttpSession session){
        return ApiResponse.<OrderTemporary>builder()
                .result(orderService.createOrderAtCartGuest(request, session))
                .build();
    }

    @PostMapping("/guest/home")
    public ApiResponse<OrderTemporary> createOrderAtHomeGuest(@RequestBody CreateOrderRequestAtHomeGuest request, HttpSession session){
        return ApiResponse.<OrderTemporary>builder()
                .result(orderService.createOrderAtHomeGuest(request, session))
                .build();
    }

    //For Admin
    @GetMapping
    public ApiResponse<List<OrderResponse>> getAll() {
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderService.getAll())
                .build();
    }
}

