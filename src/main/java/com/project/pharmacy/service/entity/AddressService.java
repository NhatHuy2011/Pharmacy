package com.project.pharmacy.service.entity;

import java.util.List;
import java.util.stream.Collectors;

import com.project.pharmacy.dto.request.delivery.GetAddressDetailRequest;
import com.project.pharmacy.dto.request.delivery.GetDistrictRequest;
import com.project.pharmacy.dto.response.delivery.*;
import com.project.pharmacy.repository.httpclient.DeliveryClient;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.project.pharmacy.dto.request.address.CreateAddressRequest;
import com.project.pharmacy.dto.request.address.UpdateAddressRequest;
import com.project.pharmacy.dto.response.entity.AddressResponse;
import com.project.pharmacy.entity.Address;
import com.project.pharmacy.entity.User;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.AddressMapper;
import com.project.pharmacy.repository.AddressRepository;
import com.project.pharmacy.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressService {
    AddressMapper addressMapper;

    UserRepository userRepository;

    AddressRepository addressRepository;

    DeliveryClient deliveryClient;

    public AddressResponse createAddress(CreateAddressRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Address address = addressMapper.toAddress(request);
        address.setUser(user);

        boolean isDuplicate = user.getAddresses().stream()
                        .anyMatch(existingAddress -> existingAddress.equals(address));

        if(isDuplicate){
            throw new AppException(ErrorCode.ADDRESS_EXISTED);
        }

        if(request.getAddressDefault()){
            for (Address address1 : user.getAddresses()){
                if (address1.getAddressDefault())
                    address1.setAddressDefault(false);
                addressRepository.save(address1);
            }
        }

        addressRepository.save(address);

        user.getAddresses().add(address);

        return addressMapper.toAddressResponse(address);
    }

    public List<AddressResponse> getAddressByUser() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return user.getAddresses().stream()
                .map(addressMapper::toAddressResponse)
                .collect(Collectors.toList());
    }

    public AddressResponse updateAddress(UpdateAddressRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        Address address = addressRepository
                .findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        if(!address.getUser().getUsername().equals(name)){
            throw new AccessDeniedException("Bạn không có quyền chỉnh sửa địa chỉ này");
        }

        addressMapper.updateAddress(address, request);
        addressRepository.save(address);

        return addressMapper.toAddressResponse(address);
    }

    @Transactional
    public void deleteAddress(String id) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        if (!address.getUser().getUsername().equals(name)) {
            throw new AccessDeniedException("Bạn không có quyền xoá địa chỉ này");
        }

        addressRepository.delete(address);
    }

    public AddressDetailResponse getAddressDetail(GetAddressDetailRequest request){
        //Tra cứu tỉnh
        DeliveryResponse<List<ProvinceGHNResponse>> provinces = deliveryClient.getListProvince();
        ProvinceGHNResponse province = provinces.getData().stream()
                .filter(item -> item.getId() == request.getProvinceId())
                .findFirst()
                .orElseThrow();

        //Tra cứu huyện
        GetDistrictRequest getDistrict = GetDistrictRequest.builder()
                .province_id(request.getProvinceId())
                .build();
        DeliveryResponse<List<DistrictGHNReponse>> districts = deliveryClient.getListDistrict(getDistrict);
        DistrictGHNReponse district = districts.getData().stream()
                .filter(item -> item.getId() == request.getDistrictId())
                .findFirst()
                .orElseThrow();

        //Tra cứu phường
        DeliveryResponse<List<WardResponse>> wards = deliveryClient.getListWard(request.getDistrictId());
        WardResponse ward = wards.getData().stream()
                .filter(item -> item.getId().equals(request.getWardCode()))
                .findFirst()
                .orElseThrow();

        AddressDetailResponse response = AddressDetailResponse.builder()
                .province(province)
                .district(district)
                .ward(ward)
                .build();

        return response;
    }
}
