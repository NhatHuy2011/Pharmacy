package com.project.pharmacy.controller;

import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.CallBackZaloPay;
import com.project.pharmacy.service.ZaloPayService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/zalopay")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ZaloPayController {
    ZaloPayService zaloPayService;

    @PostMapping("/create-payment")
    public ApiResponse<Map<String, Object>> createPaymentWithMoMo(@RequestParam String orderId) throws IOException {
        return ApiResponse.<Map<String, Object>>builder()
                .result(zaloPayService.createOrder(orderId))
                .build();
    }

    @PostMapping("/callback")
    public ApiResponse<Object> callback(@RequestBody CallBackZaloPay callBackZaloPay) throws NoSuchAlgorithmException, InvalidKeyException {
        JSONObject result = new JSONObject();
        return ApiResponse.builder()
                .result(zaloPayService.doCallBack(result, callBackZaloPay.getJsonString()))
                .build();
    }
}
