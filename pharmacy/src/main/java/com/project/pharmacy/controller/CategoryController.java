package com.project.pharmacy.controller;

import com.project.pharmacy.dto.request.CategoryCreateRequest;
import com.project.pharmacy.dto.request.CategoryUpdateRequest;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.CategoryResponse;
import com.project.pharmacy.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    CategoryService categoryService;

    //Lay danh muc goc
    @GetMapping("/null")
    public ApiResponse<List<CategoryResponse>> getRootCategory(){
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getRootCategories())
                .build();
    }

    //Lay danh muc con cua 1 danh muc
    @GetMapping("/{parentId}")
    public ApiResponse<List<CategoryResponse>> getSubCategory(@PathVariable String parentId){
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getSubCategories(parentId))
                .build();
    }

    //Them danh muc
    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestPart("createCategory") CategoryCreateRequest request,
                                                        @RequestPart("file") MultipartFile multipartFile) throws IOException {

        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.createCategory(request, multipartFile))
                .build();
    }

    //Sua danh muc
    @PutMapping("{id}")
    public ApiResponse<CategoryResponse> updateCategory(@RequestPart("updateCategory") CategoryUpdateRequest request,
                                                        @PathVariable String id,
                                                        @RequestPart("file") MultipartFile multipartFile) throws IOException{
        if(multipartFile!=null && !multipartFile.isEmpty()) {
            return ApiResponse.<CategoryResponse>builder()
                    .result(categoryService.updateCategory(id, request, multipartFile))
                    .build();
        } else{
            return ApiResponse.<CategoryResponse>builder()
                    .result(categoryService.updateCategory(id, request, null))
                    .build();
        }
    }

    @DeleteMapping("{id}")
    public ApiResponse<Objects> deleteCategory(@PathVariable String id){
        categoryService.deleteCategory(id);
        return ApiResponse.<Objects>builder()
                .message("Delete Successful")
                .build();
    }
}
