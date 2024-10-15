package com.project.pharmacy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.project.pharmacy.dto.request.CompanyCreateRequest;
import com.project.pharmacy.dto.request.CompanyUpdateRequest;
import com.project.pharmacy.dto.response.CompanyResponse;
import com.project.pharmacy.entity.Company;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompanyMapper {
    Company toCompany(CompanyCreateRequest request);

    CompanyResponse toCompanyResponse(Company company);

    void updateCompany(@MappingTarget Company company, CompanyUpdateRequest request);
}
