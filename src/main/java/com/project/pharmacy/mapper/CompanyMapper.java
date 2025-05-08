package com.project.pharmacy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.project.pharmacy.dto.request.company.CompanyCreateRequest;
import com.project.pharmacy.dto.request.company.CompanyUpdateRequest;
import com.project.pharmacy.dto.response.entity.CompanyResponse;
import com.project.pharmacy.entity.Company;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompanyMapper {
    Company toCompany(CompanyCreateRequest request);

    CompanyResponse toCompanyResponse(Company company);

    void updateCompany(@MappingTarget Company company, CompanyUpdateRequest request);
}
