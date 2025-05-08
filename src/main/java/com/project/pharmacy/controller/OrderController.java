package com.project.pharmacy.controller;

import com.project.pharmacy.dto.request.order.CreateOrderRequestAtCartGuest;
import com.project.pharmacy.dto.request.order.CreateOrderRequestAtCartUser;
import com.project.pharmacy.dto.request.order.CreateOrderRequestAtHomeGuest;
import com.project.pharmacy.dto.request.order.CreateOrderRequestAtHomeUser;
import com.project.pharmacy.dto.response.common.ApiResponse;
import com.project.pharmacy.dto.response.entity.OrderResponse;
import com.project.pharmacy.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        return ApiResponse.<List<OrderResponse>>builder()
                .result(orderResponses)
                .build();
    }

    //For Guest
    @PostMapping("/guest/cart")
    public ApiResponse<OrderResponse> createOrderAtCartGuest(@RequestBody @Valid CreateOrderRequestAtCartGuest request, HttpSession session){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrderAtCartGuest(request, session))
                .build();
    }

    @PostMapping("/guest/home")
    public ApiResponse<OrderResponse> createOrderAtHomeGuest(@RequestBody @Valid CreateOrderRequestAtHomeGuest request, HttpSession session){
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrderAtHomeGuest(request, session))
                .build();
    }

    //For Employee and Admin
    @GetMapping("/success")
    public ApiResponse<Page<OrderResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(defaultValue = "asc") String sortBy) {
        Sort.Order isConfirmOrder = sortBy.equals("desc")
                ? Sort.Order.desc("isConfirm")
                : Sort.Order.asc("isConfirm");

        Sort.Order orderDateOrder = Sort.Order.asc("orderDate");

        Sort sort = Sort.by(isConfirmOrder, orderDateOrder);
        Pageable pageable = PageRequest.of(page, size, sort);
        return ApiResponse.<Page<OrderResponse>>builder()
                .result(orderService.getAllByStatus(pageable))
                .build();
    }

    @GetMapping("/cod")
    public ApiResponse<Page<OrderResponse>> getAllOrderCOD(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(defaultValue = "asc") String sortBy) {
        Sort.Order isConfirmOrder = sortBy.equals("desc")
                ? Sort.Order.desc("isConfirm")
                : Sort.Order.asc("isConfirm");

        Sort.Order orderDateOrder = Sort.Order.asc("orderDate");

        Sort sort = Sort.by(isConfirmOrder, orderDateOrder);
        Pageable pageable = PageRequest.of(page, size, sort);
        return ApiResponse.<Page<OrderResponse>>builder()
                .result(orderService.getAllOrderCOD(pageable))
                .build();
    }

    @PutMapping("{id}")
    public ApiResponse<Void> confirmOrder(@PathVariable String id){
        orderService.confirmOrders(id);
        return ApiResponse.<Void>builder()
                .message("Cập nhật đơn hàng thành công")
                .build();
    }
    
    //Public
    @GetMapping("{id}")
    public ApiResponse<OrderResponse> getOrderDetails(@PathVariable String id) {
        return ApiResponse.<OrderResponse>builder()
                .result(orderService.getOrderDetails(id))
                .build();
    }
}

