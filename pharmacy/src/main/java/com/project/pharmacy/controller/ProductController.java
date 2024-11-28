package com.project.pharmacy.controller;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.project.pharmacy.dto.request.ProductCreateRequest;
import com.project.pharmacy.dto.request.ProductUpdateRequest;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.ProductResponse;
import com.project.pharmacy.service.ProductService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    // Role ADMIN and EMPLOYEE
    @PostMapping
    public ApiResponse<ProductResponse> createProduct(
            @RequestPart("createProduct") @Valid ProductCreateRequest request,
            @RequestPart("listImages") List<MultipartFile> multipartFiles)
            throws IOException {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.createProduct(request, multipartFiles))
                .build();
    }

    @PutMapping
    public ApiResponse<ProductResponse> updateProduct(
            @RequestPart("updateProduct") ProductUpdateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files)
            throws IOException {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.updateProduct(request, files))
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<Objects> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ApiResponse.<Objects>builder()
                .message("Delete Product Successful")
                .build();
    }

    // Role USER
    @GetMapping
    public ApiResponse<Page<ProductResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        Sort sort = sortOrder.equals("desc")
                ? Sort.by("prices.price").descending()
                : Sort.by("prices.price").ascending();
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> productResponses = productService.getAllProduct(pageable);

        return ApiResponse.<Page<ProductResponse>>builder()
                .result(productResponses)
                .build();
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<Page<ProductResponse>> getProductByCategory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @PathVariable String categoryId) {
        /*Sort sort = sortOrder.equals("desc")
                ? Sort.by("name").descending()
                : Sort.by("name").ascending();*/
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> productResponses;
        if(sortOrder.equals("asc")) {
            productResponses = productService.getProductByCategoryAsc(pageable, categoryId);
        }
        else {
            productResponses = productService.getProductByCategoryDesc(pageable, categoryId);
        }
        return ApiResponse.<Page<ProductResponse>>builder()
                .result(productResponses)
                .build();
    }

    @GetMapping("{id}")
    public ApiResponse<List<ProductResponse>> getOne(@PathVariable String id) {
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getOne(id))
                .build();
    }
}
