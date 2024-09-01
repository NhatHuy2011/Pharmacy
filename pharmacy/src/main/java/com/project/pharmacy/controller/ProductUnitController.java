package com.project.pharmacy.controller;

import com.project.pharmacy.dto.request.ProductUnitCreateRequest;
import com.project.pharmacy.dto.request.ProductUnitUpdateRequest;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.ProductUnitResponse;
import com.project.pharmacy.service.ProductUnitService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/product-unit")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductUnitController {
    ProductUnitService productUnitService;

    @PostMapping
    public ApiResponse<ProductUnitResponse> createProductUnit(@Valid @RequestBody ProductUnitCreateRequest request){
        return ApiResponse.<ProductUnitResponse>builder()
                .result(productUnitService.creatProductUnit(request))
                .build();
    }

    @PutMapping
    public ApiResponse<Objects> updateProductUnit(@RequestBody ProductUnitUpdateRequest request){
        productUnitService.updateProductUnit(request);
        return ApiResponse.<Objects>builder()
                .message("Update Successful")
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<Objects> deleteProductUnit(@PathVariable String id) {
        productUnitService.deleteProductUnit(id);
        return ApiResponse.<Objects>builder()
                .message("Delete Successful")
                .build();
    }
}
