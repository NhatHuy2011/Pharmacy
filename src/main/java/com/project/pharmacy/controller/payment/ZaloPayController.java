package com.project.pharmacy.controller.payment;

import com.project.pharmacy.dto.response.common.ApiResponse;
import com.project.pharmacy.service.payment.ZaloPayService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
}
