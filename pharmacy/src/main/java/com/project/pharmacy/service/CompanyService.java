package com.project.pharmacy.service;

import java.io.IOException;
import java.util.List;

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
import com.project.pharmacy.entity.Product;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.CompanyMapper;
import com.project.pharmacy.repository.CompanyRepository;
import com.project.pharmacy.repository.ImageRepository;
import com.project.pharmacy.repository.ProductRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyService {
    CompanyRepository companyRepository;

    ProductRepository productRepository;

    CloudinaryService cloudinaryService;

    ImageRepository imageRepository;

    CompanyMapper companyMapper;
    // ADMIN and EMPLOYEE
    // Them cong ty
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
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
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
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
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCompany(String id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));
        List<Product> products = productRepository.findByCompanyId(id);
        for (Product product : products) {
            imageRepository.deleteAllByProductId(product.getId());
        }
        productRepository.deleteAllByCompanyId(id);
        companyRepository.deleteById(company.getId());
    }

    // Role USER
    // Xem danh sach cong ty
    public Page<CompanyResponse> getCompany(Pageable pageable) {
        return companyRepository.findAll(pageable).map(companyMapper::toCompanyResponse);
    }
}
