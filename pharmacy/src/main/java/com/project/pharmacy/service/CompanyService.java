package com.project.pharmacy.service;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.pharmacy.dto.request.CompanyCreateRequest;
import com.project.pharmacy.dto.request.CompanyUpdateRequest;
import com.project.pharmacy.dto.response.CompanyResponse;
import com.project.pharmacy.entity.Company;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.CompanyMapper;
import com.project.pharmacy.repository.CompanyRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyService {
    CompanyRepository companyRepository;

    CloudinaryService cloudinaryService;

    CompanyMapper companyMapper;
    // ADMIN and EMPLOYEE
    // Them cong ty
    @PreAuthorize("hasRole('ADMIN')")
    public CompanyResponse createCompany(CompanyCreateRequest request, MultipartFile files) throws IOException {
        if (companyRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.COMPANY_EXISTED);
        String url = cloudinaryService.uploadImage(files);

        Company company = companyMapper.toCompany(request);
        company.setImage(url);
        companyRepository.save(company);

        return companyMapper.toCompanyResponse(company);
    }

    // Sua cong ty
    @PreAuthorize("hasRole('ADMIN')")
    public CompanyResponse updateCompany(CompanyUpdateRequest request, MultipartFile files) throws IOException {
        Company company = companyRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

        if (files != null && !files.isEmpty()) {
            String url = cloudinaryService.uploadImage(files);
            company.setImage(url);
        }

        companyMapper.updateCompany(company, request);
        companyRepository.save(company);

        return companyMapper.toCompanyResponse(company);
    }

    // Role ADMIN
    // Xoa cong ty
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCompany(String id) {
        companyRepository.deleteById(id);
    }

    // Role USER
    // Xem danh sach cong ty
    public Page<CompanyResponse> getCompany(Pageable pageable) {
        return companyRepository.findAll(pageable)
                .map(companyMapper::toCompanyResponse);
    }
}
