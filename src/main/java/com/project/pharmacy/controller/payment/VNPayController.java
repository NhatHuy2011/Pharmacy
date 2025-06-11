package com.project.pharmacy.controller.payment;

import com.project.pharmacy.dto.response.common.ApiResponse;
import com.project.pharmacy.dto.response.payment.RefundPaymentResponse;
import com.project.pharmacy.service.payment.VNPayService;
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
                .result(vnPayService.createPaymentVNPayWeb(req))
                .build();
    }

    @PostMapping("/refund")
    public ApiResponse<RefundPaymentResponse> refundPayment(HttpServletRequest req) {
        return ApiResponse.<RefundPaymentResponse>builder()
                .message("Success")
                .result(vnPayService.refundVNPay(req))
                .build();
    }
}
