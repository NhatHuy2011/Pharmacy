package com.project.pharmacy.controller.entity;

import java.util.List;

import com.project.pharmacy.dto.request.delivery.GetAddressDetailRequest;
import com.project.pharmacy.dto.response.delivery.AddressDetailResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import com.project.pharmacy.dto.request.address.CreateAddressRequest;
import com.project.pharmacy.dto.request.address.UpdateAddressRequest;
import com.project.pharmacy.dto.response.entity.AddressResponse;
import com.project.pharmacy.dto.response.common.ApiResponse;
import com.project.pharmacy.service.entity.AddressService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AddressController {
    AddressService addressService;

    @PostMapping
    public ApiResponse<AddressResponse> createAddress(@RequestBody @Valid CreateAddressRequest request) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.createAddress(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<AddressResponse>> getAddressByUser() {
        return ApiResponse.<List<AddressResponse>>builder()
                .result(addressService.getAddressByUser())
                .build();
    }

    @PutMapping
    public ApiResponse<AddressResponse> updateAddressByUser(@RequestBody UpdateAddressRequest request) {
        return ApiResponse.<AddressResponse>builder()
                .result(addressService.updateAddress(request))
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable String id) {
        addressService.deleteAddress(id);
        return ApiResponse.<Void>builder()
                .message("Delete address successful")
                .build();
    }

    @GetMapping("/detail")
    public ApiResponse<AddressDetailResponse> getAddressDetail(@RequestParam("provinceId") int provinceId,
                                                               @RequestParam("districtId") int districtId,
                                                               @RequestParam("wardCode") String wardCode){
        GetAddressDetailRequest request = new GetAddressDetailRequest();
        request.setProvinceId(provinceId);
        request.setDistrictId(districtId);
        request.setWardCode(wardCode);
        return ApiResponse.<AddressDetailResponse>builder()
                .result(addressService.getAddressDetail(request))
                .build();
    }
}
