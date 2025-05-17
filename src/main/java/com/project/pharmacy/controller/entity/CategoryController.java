package com.project.pharmacy.controller.entity;

import java.io.IOException;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.project.pharmacy.dto.request.category.CategoryCreateRequest;
import com.project.pharmacy.dto.request.category.CategoryUpdateRequest;
import com.project.pharmacy.dto.response.common.ApiResponse;
import com.project.pharmacy.dto.response.entity.CategoryResponse;
import com.project.pharmacy.service.entity.CategoryService;

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
    public ApiResponse<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ApiResponse.<Void>builder()
                .message("Delete Successful")
                .build();
    }
}
