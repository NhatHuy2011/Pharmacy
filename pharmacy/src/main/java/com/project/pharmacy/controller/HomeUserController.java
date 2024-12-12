package com.project.pharmacy.controller;

import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.CompanyResponse;
import com.project.pharmacy.dto.response.ProductResponse;
import com.project.pharmacy.service.HomeUserService;
import com.project.pharmacy.service.ProductService;
import com.project.pharmacy.utils.TopCompany;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomeUserController {
    HomeUserService homeUserService;

    @GetMapping("/top20")
    public ApiResponse<List<ProductResponse>> getTop20NewProduct(){
        return ApiResponse.<List<ProductResponse>>builder()
                .result(homeUserService.getTop20NewProduct())
                .build();
    }

    @GetMapping("/bestSeller")
    public ApiResponse<List<ProductResponse>> getBestSeller(){
        return ApiResponse.<List<ProductResponse>>builder()
                .result(homeUserService.getTop20ProductBestSeller())
                .build();
    }

    @GetMapping("/topCompany")
    public ApiResponse<List<CompanyResponse>> getTopCompany(){
        return ApiResponse.<List<CompanyResponse>>builder()
                .result(homeUserService.getTop20Company())
                .build();
    }
}
