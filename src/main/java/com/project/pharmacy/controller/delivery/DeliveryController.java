package com.project.pharmacy.controller.delivery;

import com.project.pharmacy.dto.request.delivery.CalculateDeliveryFeeRequest;
import com.project.pharmacy.dto.request.delivery.GetAvailableServiceRequest;
import com.project.pharmacy.dto.request.delivery.GetDistrictRequest;
import com.project.pharmacy.dto.response.delivery.*;
import com.project.pharmacy.repository.httpclient.DeliveryClient;
import com.project.pharmacy.service.delivery.DeliveryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/delivery")
public class DeliveryController {
    DeliveryClient deliveryClient;

    DeliveryService deliveryService;

    @GetMapping("/province")
    DeliveryResponse<List<ProvinceGHNResponse>> getProvince(){
        return deliveryClient.getListProvince();
    }

    @GetMapping("/district")
    DeliveryResponse<List<DistrictGHNReponse>> getDistrict(@RequestParam int provinceId){
        GetDistrictRequest request = GetDistrictRequest.builder()
                .province_id(provinceId)
                .build();
        return deliveryClient.getListDistrict(request);
    }

    @GetMapping("/ward")
    DeliveryResponse<List<WardResponse>> getListWard(@RequestParam int districtId){
        return deliveryClient.getListWard(districtId);
    }

    @PostMapping("/service")
    DeliveryResponse<List<AvailableServiceResponse>> getAvailableService(@RequestBody GetAvailableServiceRequest request){
        return deliveryService.getAvailableService(request);
    }

    @PostMapping("/calculate/fee")
    DeliveryResponse<CalculateDeliveryOrderFeeResponse> getCalculateFee(@RequestBody CalculateDeliveryFeeRequest request){
        return deliveryService.calculateFeeDeliveryOrder(request);
    }
}
