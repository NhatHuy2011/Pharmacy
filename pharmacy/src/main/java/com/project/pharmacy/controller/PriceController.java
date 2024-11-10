package com.project.pharmacy.controller;

import java.util.Objects;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.project.pharmacy.dto.request.PriceCreateRequest;
import com.project.pharmacy.dto.request.PriceUpdateRequest;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.PriceResponse;
import com.project.pharmacy.service.PriceService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/price")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PriceController {
    PriceService priceService;

    // Role ADMIN and USER
    @PostMapping
    public ApiResponse<PriceResponse> createPrice(@Valid @RequestBody PriceCreateRequest request) {
        return ApiResponse.<PriceResponse>builder()
                .result(priceService.createPrice(request))
                .build();
    }

    @PutMapping
    public ApiResponse<Objects> updatePrice(@RequestBody PriceUpdateRequest request) {
        priceService.updatePrice(request);
        return ApiResponse.<Objects>builder().message("Update Successful").build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<Objects> deletePrice(@PathVariable String id) {
        priceService.deletePrice(id);
        return ApiResponse.<Objects>builder().message("Delete Successful").build();
    }
}
