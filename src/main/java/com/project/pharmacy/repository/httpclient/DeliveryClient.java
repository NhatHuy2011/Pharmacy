package com.project.pharmacy.repository.httpclient;

import com.project.pharmacy.configuration.FeignHeaderDeliveryInterceptor;
import com.project.pharmacy.dto.request.delivery.GetAvailableServiceRequest;
import com.project.pharmacy.dto.request.delivery.GetDistrictRequest;
import com.project.pharmacy.dto.response.delivery.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "delivery-client",
        url = "https://dev-online-gateway.ghn.vn/shiip/public-api",
        configuration = FeignHeaderDeliveryInterceptor.class
)
public interface DeliveryClient {
    //GET ADDRESS
    @GetMapping(value = "/master-data/province", produces = MediaType.APPLICATION_JSON_VALUE)
    DeliveryResponse<List<ProvinceGHNResponse>> getListProvince();

    @GetMapping(value = "/master-data/district", produces = MediaType.APPLICATION_JSON_VALUE)
    DeliveryResponse<List<DistrictGHNReponse>> getListDistrict(@RequestBody GetDistrictRequest request);

    @GetMapping(value = "/master-data/ward", produces = MediaType.APPLICATION_JSON_VALUE)
    DeliveryResponse<List<WardResponse>> getListWard(@RequestParam String district_id);

    //GET SERVICE
    @PostMapping(value = "/v2/shipping-order/available-services", produces = MediaType.APPLICATION_JSON_VALUE)
    DeliveryResponse<AvailableServiceResponse> getAvailableService(@RequestBody GetAvailableServiceRequest request);
}
