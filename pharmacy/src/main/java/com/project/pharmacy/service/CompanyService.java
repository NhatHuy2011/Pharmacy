package com.project.pharmacy.service;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyService {
    CompanyRepository companyRepository;

    ProductRepository productRepository;

    ImageService imageService;

    ImageRepository imageRepository;

    CompanyMapper companyMapper;
    //Them cong ty
    @PreAuthorize("hasRole('ADMIN')")
    public CompanyResponse createCompany(CompanyCreateRequest request, MultipartFile files) throws IOException {
        if(companyRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.COMPANY_EXISTED);
        String url = imageService.uploadImage(files);

        Company company = companyMapper.toCompany(request);
        company.setImage(url);
        companyRepository.save(company);

        return companyMapper.toCompanyResponse(company);
    }

    //Xem danh sach cong ty
    @PreAuthorize("hasRole('ADMIN')")
    public List<CompanyResponse> getCompany(){
        return companyRepository.findAll().stream()
                .map(companyMapper::toCompanyResponse)
                .collect(Collectors.toList());
    }

    //Sua cong ty
    @PreAuthorize("hasRole('ADMIN')")
    public CompanyResponse updateCompany(CompanyUpdateRequest request, MultipartFile files) throws IOException{
        Company company = companyRepository.findById(request.getId())
                .orElseThrow(()->new AppException(ErrorCode.COMPANY_NOT_FOUND));
        if(files != null && !files.isEmpty()){
            String url = imageService.uploadImage(files);

            //Mapper
            companyMapper.updateCompany(company, request);
            company.setImage(url);
        } else{
            companyMapper.updateCompany(company, request);
        }
        companyRepository.save(company);

        return companyMapper.toCompanyResponse(company);
    }

    //Xoa cong ty
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCompany(String id){
        Company company = companyRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.COMPANY_NOT_FOUND));
        List<Product> products = productRepository.findByCompanyId(id);
        for(Product product: products){
            imageRepository.deleteAllByProductId(product.getId());
        }
        productRepository.deleteAllByCompanyId(id);
        companyRepository.deleteById(company.getId());
    }
}
