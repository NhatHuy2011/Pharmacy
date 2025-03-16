package com.project.pharmacy.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.*;

import com.project.pharmacy.dto.request.AddToCartRequest;
import com.project.pharmacy.dto.request.DeleteCartItemRequest;
import com.project.pharmacy.dto.request.UpdateCartRequest;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.CartResponse;
import com.project.pharmacy.service.CartService;
import com.project.pharmacy.utils.CartTemporary;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @PostMapping
    public ApiResponse<Void> addToCartForUser(@RequestBody AddToCartRequest request) {
        cartService.addToCartForUser(request);
        return ApiResponse.<Void>builder()
                .message("Add to cart success")
                .build();
    }

    @GetMapping
    public ApiResponse<CartResponse> getCartForUser() {
        CartResponse cartResponse = cartService.getCartForUser();
        return ApiResponse.<CartResponse>builder()
                .result(cartResponse)
                .build();
    }

    @PutMapping
    public ApiResponse<Void> updateCartForUser(@RequestBody UpdateCartRequest request) {
        cartService.updateCartForUser(request);
        return ApiResponse.<Void>builder()
                .message("Update cart successful")
                .build();
    }

    @DeleteMapping
    public ApiResponse<Void> deleteCartForUser(@RequestBody DeleteCartItemRequest request) {
        cartService.deleteCartItemForUser(request);
        return ApiResponse.<Void>builder()
                .message("Delete cart item succcessful")
                .build();
    }

    @PostMapping("/guest")
    public ApiResponse<Void> addToCartForGuest(@RequestBody AddToCartRequest request, HttpSession session) {
        cartService.addToCartForGuest(request, session);
        return ApiResponse.<Void>builder()
                .message("Add to cart successful")
                .build();
    }

    @GetMapping("/guest")
    public ApiResponse<CartTemporary> getCartForGuest(HttpSession session) {
        CartTemporary cartTemporary = cartService.getCartForGuest(session);
        return ApiResponse.<CartTemporary>builder()
                .result(cartTemporary)
                .build();

    }

    @PutMapping("/guest")
    public ApiResponse<Void> updateCartForGuest(@RequestBody UpdateCartRequest request, HttpSession session) {
        cartService.updateCartForGuest(request, session);
        return ApiResponse.<Void>builder()
                .message("Update cart successful")
                .build();
    }

    @DeleteMapping("/guest")
    public ApiResponse<Void> deleteCartItemForGuest(@RequestBody DeleteCartItemRequest request, HttpSession session) {
        cartService.deleteCartItemForGuest(request, session);
        return ApiResponse.<Void>builder()
                .message("Delete cart item successful")
                .build();
    }

    @PostMapping("/transfer")
    public ApiResponse<Void> transferCart(HttpSession session) {
        cartService.transferGuestCartToUserCart(session);
        return ApiResponse.<Void>builder()
                .message("Transfer successful")
                .build();
    }
}
