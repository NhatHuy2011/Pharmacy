package com.project.pharmacy.service.delivery;

import com.project.pharmacy.dto.request.delivery.CalculateDeliveryFeeRequest;
import com.project.pharmacy.dto.request.delivery.GetAvailableServiceRequest;
import com.project.pharmacy.dto.response.delivery.AvailableServiceResponse;
import com.project.pharmacy.dto.response.delivery.CalculateDeliveryOrderFeeResponse;
import com.project.pharmacy.dto.response.delivery.DeliveryResponse;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.httpclient.DeliveryClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeliveryService {
    DeliveryClient deliveryClient;

    @NonFinal
    @Value("${ghn.token}")
    String token;

    @NonFinal
    @Value("${ghn.shop_id}")
    int shopId;

    @NonFinal
    @Value("${ghn.api}")
    String api;

    @NonFinal
    @Value("${ghn.district_id}")
    int districtId;

    RestTemplate restTemplate = new RestTemplate();

    public DeliveryResponse<List<AvailableServiceResponse>> getAvailableService(GetAvailableServiceRequest request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", token);

        Map<String, Integer> body = new HashMap<>();
        body.put("from_district", districtId);
        body.put("to_district", request.getTo_district());
        body.put("shop_id", shopId);

        HttpEntity<Map<String, Integer>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<DeliveryResponse<List<AvailableServiceResponse>>> response = restTemplate.exchange(
                api +"/v2/shipping-order/available-services",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        if(response.getBody().getData() == null | response.getBody().getData().size() == 0)
            throw new AppException(ErrorCode.DELIVERY_SERVICE_NOT_AVAILABLE);

        return response.getBody();
    }

    public DeliveryResponse<CalculateDeliveryOrderFeeResponse> calculateFeeDeliveryOrder(CalculateDeliveryFeeRequest request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Token", token);
        headers.set("ShopId", String.valueOf(shopId));

        Map<String, Object> body = new HashMap<>();
        body.put("service_id", request.getService_id());
        body.put("insurance_value", request.getInsurance_value());
        body.put("to_ward_code", request.getTo_ward_code());
        body.put("to_district_id", request.getTo_district_id());
        body.put("from_district", districtId);
        body.put("weight", 1000);
        body.put("height", 20);
        body.put("lenght", 30);
        body.put("width", 40);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<DeliveryResponse<CalculateDeliveryOrderFeeResponse>> response = restTemplate.exchange(
                api +"/v2/shipping-order/fee",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }
}
