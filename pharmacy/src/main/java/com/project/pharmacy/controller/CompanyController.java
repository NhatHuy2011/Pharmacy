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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ApiResponse<CompanyResponse> createCompany(@Valid @RequestPart("createCompany") CompanyCreateRequest request,
                                                      @RequestPart("files") MultipartFile files) throws IOException {
        return ApiResponse.<CompanyResponse>builder()
                .result(companyService.createCompany(request, files))
                .build();
    }

    @PutMapping("{id}")
    public ApiResponse<CompanyResponse> updateCompany(@RequestPart("updateCompany") CompanyUpdateRequest request,
                                                      @PathVariable String id,
                                                      @RequestPart("files") MultipartFile files) throws IOException{
        if(files!=null && !files.isEmpty()) {
            return ApiResponse.<CompanyResponse>builder()
                    .result(companyService.updateCompany(request, id, files))
                    .build();
        }
        else {
            return ApiResponse.<CompanyResponse>builder()
                    .result(companyService.updateCompany(request, id, null))
                    .build();
        }
    }

    @DeleteMapping("{id}")
    public ApiResponse<Objects> deleteCompany(@PathVariable("id") String id){
        companyService.deleteCompany(id);
        return ApiResponse.<Objects>builder()
                .message("Delete Company Successful")
                .build();
    }
}
