package com.project.pharmacy.controller;

import com.project.pharmacy.dto.request.CompanyCreateRequest;
import com.project.pharmacy.dto.request.CompanyUpdateRequest;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.CompanyResponse;
import com.project.pharmacy.service.CompanyService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyController {
    CompanyService companyService;

    @GetMapping
    public ApiResponse<List<CompanyResponse>> getCompany(){
        return ApiResponse.<List<CompanyResponse>>builder()
                .result(companyService.getCompany())
                .build();
    }

    @PostMapping
    public ApiResponse<CompanyResponse> createCompany(@Valid @RequestBody CompanyCreateRequest request){
        return ApiResponse.<CompanyResponse>builder()
                .result(companyService.createCompany(request))
                .build();
    }

    @PutMapping("{id}")
    public ApiResponse<CompanyResponse> updateCompany(@RequestBody CompanyUpdateRequest request, @PathVariable String id){
        return ApiResponse.<CompanyResponse>builder()
                .result(companyService.updateCompany(request, id))
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<Objects> deleteCompany(@PathVariable("id") String id){
        companyService.deleteCompany(id);
        return ApiResponse.<Objects>builder()
                .message("Delete Company Successful")
                .build();
    }
}
