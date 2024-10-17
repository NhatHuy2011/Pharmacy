package com.project.pharmacy.controller;

import com.project.pharmacy.dto.request.AddToCartRequest;
import com.project.pharmacy.dto.request.DeleteCartItemRequest;
import com.project.pharmacy.dto.request.UpdateCartRequest;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.CartResponse;
import com.project.pharmacy.service.CartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartController {
    CartService cartService;

    @PostMapping
    public ApiResponse<Void> addToCartForUser(@RequestBody AddToCartRequest request){
        cartService.addToCartForUser(request);
        return ApiResponse.<Void>builder()
                .message("Add to cart success")
                .build();
    }

    @GetMapping
    public ApiResponse<CartResponse> getCartForUser(){
        return ApiResponse.<CartResponse>builder()
                .result(cartService.getCartForUser())
                .build();
    }

    @PutMapping
    public ApiResponse<Void> updateCartForUser(@RequestBody UpdateCartRequest request){
        cartService.updateCartForUser(request);
        return ApiResponse.<Void>builder()
                .message("Update cart successful")
                .build();
    }

    @DeleteMapping
    public ApiResponse<Void> deleteCartForUser(@RequestBody DeleteCartItemRequest request){
        cartService.deleteCartItemForUser(request);
        return ApiResponse.<Void>builder()
                .message("Delete cart item succcessful")
                .build();
    }
}
