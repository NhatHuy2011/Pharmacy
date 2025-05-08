package com.project.pharmacy.controller.home;

import com.project.pharmacy.dto.response.common.ApiResponse;
import com.project.pharmacy.dto.response.entity.CategoryResponse;
import com.project.pharmacy.dto.response.entity.CompanyResponse;
import com.project.pharmacy.dto.response.entity.ProductResponse;
import com.project.pharmacy.dto.response.home.HomeResponse;
import com.project.pharmacy.service.CategoryService;
import com.project.pharmacy.service.home.HomeUserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/home/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomeUserController {
    HomeUserService homeUserService;

    CategoryService categoryService;

    @GetMapping
    public ApiResponse<HomeResponse> getHome(){
        List<ProductResponse> newProducts = homeUserService.getTop10NewProduct();
        List<ProductResponse> topProducts = homeUserService.getTop10ProductBestSeller();
        List<CompanyResponse> topCompanies = homeUserService.getTop10Company();
        List<CategoryResponse> categories = categoryService.getRootCategories();

        HomeResponse homeResponse = HomeResponse.builder()
                .categories(categories)
                .newProducts(newProducts)
                .topProducts(topProducts)
                .topCompanies(topCompanies)
                .build();

        return ApiResponse.<HomeResponse>builder()
                .result(homeResponse)
                .build();
    }
}
