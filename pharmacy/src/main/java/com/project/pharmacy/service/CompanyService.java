package com.project.pharmacy.service;

import com.project.pharmacy.dto.request.CompanyCreateRequest;
import com.project.pharmacy.dto.request.CompanyUpdateRequest;
import com.project.pharmacy.dto.response.CompanyResponse;
import com.project.pharmacy.entity.Company;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.CompanyRepository;
import com.project.pharmacy.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CompanyService {
    CompanyRepository companyRepository;

    ProductRepository productRepository;
    //Them cong ty
    public CompanyResponse createCompany(CompanyCreateRequest request){
        if(companyRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.COMPANY_EXISTED);
        Company company = new Company();
        company.setName(request.getName());
        company.setOrigin(request.getOrigin());
        companyRepository.save(company);

        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .origin(company.getOrigin())
                .build();
    }

    //Xem danh sach cong ty
    public List<CompanyResponse> getCompany(){
        List<Company> companies = companyRepository.findAll();
        List<CompanyResponse> companyResponses = new ArrayList<>();

        for (Company company : companies){
            CompanyResponse temp = CompanyResponse.builder()
                    .id(company.getId())
                    .name(company.getName())
                    .origin(company.getOrigin())
                    .build();
            companyResponses.add(temp);
        }
        return companyResponses;
    }

    //Sua cong ty
    public CompanyResponse updateCompany(CompanyUpdateRequest request, String id){
        Company company = companyRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.COMPANY_NOT_FOUND));
        company.setName(request.getName());
        company.setOrigin(request.getOrigin());
        companyRepository.save(company);
        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .origin(company.getOrigin())
                .build();
    }

    //Xoa cong ty
    @Transactional
    public void deleteCompany(String id){
        Company company = companyRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.COMPANY_NOT_FOUND));

        productRepository.updateCompanyIdToNull(id);
        companyRepository.deleteById(id);
    }
}
