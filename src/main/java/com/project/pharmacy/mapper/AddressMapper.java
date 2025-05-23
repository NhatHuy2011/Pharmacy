package com.project.pharmacy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.project.pharmacy.dto.request.address.CreateAddressRequest;
import com.project.pharmacy.dto.request.address.UpdateAddressRequest;
import com.project.pharmacy.dto.response.entity.AddressResponse;
import com.project.pharmacy.entity.Address;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AddressMapper {
    Address toAddress(CreateAddressRequest request);

    AddressResponse toAddressResponse(Address address);

    void updateAddress(@MappingTarget Address address, UpdateAddressRequest request);
}
