package com.project.pharmacy.controller.entity;

import java.io.IOException;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.project.pharmacy.dto.request.company.CompanyCreateRequest;
import com.project.pharmacy.dto.request.company.CompanyUpdateRequest;
import com.project.pharmacy.dto.response.common.ApiResponse;
import com.project.pharmacy.dto.response.entity.CompanyResponse;
import com.project.pharmacy.service.entity.CompanyService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyController {
    CompanyService companyService;

    // Role USER
    @GetMapping
    public ApiResponse<Page<CompanyResponse>> getCompany(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CompanyResponse> companyResponses = companyService.getCompany(pageable);
        return ApiResponse.<Page<CompanyResponse>>builder()
                .result(companyResponses)
                .build();
    }

    // Role ADMIN and USER
    @PostMapping
    public ApiResponse<CompanyResponse> createCompany(
            @Valid @RequestPart("createCompany") CompanyCreateRequest request,
            @RequestPart("files") MultipartFile files)
            throws IOException {
        return ApiResponse.<CompanyResponse>builder()
                .result(companyService.createCompany(request, files))
                .build();
    }

    @PutMapping
    public ApiResponse<CompanyResponse> updateCompany(
            @RequestPart("updateCompany") CompanyUpdateRequest request,
            @RequestPart(value = "files", required = false) MultipartFile files)
            throws IOException {
        return ApiResponse.<CompanyResponse>builder()
                .result(companyService.updateCompany(request, files))
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<Void> deleteCompany(@PathVariable String id) {
        companyService.deleteCompany(id);
        return ApiResponse.<Void>builder()
                .message("Delete Company Successful")
                .build();
    }
}
