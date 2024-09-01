package com.project.pharmacy.controller;

import com.project.pharmacy.dto.request.ProductCreateRequest;
import com.project.pharmacy.dto.request.ProductUpdateRequest;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.ProductResponse;
import com.project.pharmacy.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
    ProductService productService;

    @PostMapping
    public ApiResponse<ProductResponse> createProduct(@RequestPart("createProduct") ProductCreateRequest request,
                                                      @RequestPart("listImages") List<MultipartFile> multipartFiles) throws IOException {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.createProduct(request, multipartFiles))
                .build();
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getAll(){
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getAllProduct())
                .build();
    }

    @GetMapping("{id}")
    public ApiResponse<ProductResponse> getOne(@PathVariable String id){
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getOne(id))
                .build();
    }

    @PutMapping
    public ApiResponse<ProductResponse> updateProduct(@RequestPart("updateProduct") ProductUpdateRequest request,
                                                      @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException{
        if (files != null && !files.isEmpty()) {
            // Xử lý hình ảnh nếu có
            return ApiResponse.<ProductResponse>builder()
                    .result(productService.updateProduct(request, files))
                    .build();
        } else {
            // Xử lý trường hợp không có hình ảnh
            return ApiResponse.<ProductResponse>builder()
                    .result(productService.updateProduct(request, null))
                    .build();
        }
    }

    @DeleteMapping("{id}")
    public ApiResponse<Objects> deleteProduct(@PathVariable String id){
        productService.deleteProduct(id);
        return ApiResponse.<Objects>builder()
                .message("Delete Product Successful")
                .build();
    }
}
