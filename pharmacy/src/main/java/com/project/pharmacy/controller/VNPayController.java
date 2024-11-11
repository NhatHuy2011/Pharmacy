package com.project.pharmacy.controller;

import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vnpay")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VNPayController {
    VNPayService vnPayService;

    @PostMapping("/create-payment")
    public ApiResponse<String> createPayment(HttpServletRequest req) {
        return ApiResponse.<String>builder()
                .message("Success")
                .result(vnPayService.createPaymentVNPay(req))
                .build();
    }

    @GetMapping("/callback")
    public ApiResponse<Void> callback(@RequestParam(value = "vnp_ResponseCode") String responseCode,
                                      @RequestParam(value = "vnp_TxnRef") String orderId) {
        String message;
        if (responseCode.equals("00")){
            message = "Thanh toán thành công";
        }else {
            message = "Thanh toán thất bại";
        }
        vnPayService.callBack(responseCode, orderId);
        return ApiResponse.<Void>builder()
                .message(message)
                .build();
    }
}
