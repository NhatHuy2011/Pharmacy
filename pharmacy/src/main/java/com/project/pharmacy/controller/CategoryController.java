package com.project.pharmacy.controller;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.project.pharmacy.dto.request.CategoryCreateRequest;
import com.project.pharmacy.dto.request.CategoryUpdateRequest;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.CategoryResponse;
import com.project.pharmacy.service.CategoryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    // Role USER
    // Lay danh muc goc
    @GetMapping("/null")
    public ApiResponse<List<CategoryResponse>> getRootCategory() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getRootCategories())
                .build();
    }

    // Lay danh muc con cua 1 danh muc
    @GetMapping("/{parentId}")
    public ApiResponse<List<CategoryResponse>> getSubCategory(@PathVariable String parentId) {
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getSubCategories(parentId))
                .build();
    }

    // Role ADMIN
    // Them danh muc
    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(
            @Valid @RequestPart("createCategory") CategoryCreateRequest request,
            @RequestPart("file") MultipartFile multipartFile)
            throws IOException {

        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.createCategory(request, multipartFile))
                .build();
    }

    //Xem danh sach danh muc
    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAll(){
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getAll())
                .build();
    }

    // Sua danh muc
    @PutMapping
    public ApiResponse<CategoryResponse> updateCategory(
            @RequestPart("updateCategory") CategoryUpdateRequest request,
            @RequestPart(value = "file", required = false) MultipartFile multipartFile)
            throws IOException {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.updateCategory(request, multipartFile))
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<Objects> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ApiResponse.<Objects>builder()
                .message("Delete Successful").build();
    }
}
