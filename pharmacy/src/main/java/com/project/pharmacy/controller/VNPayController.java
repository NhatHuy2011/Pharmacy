package com.project.pharmacy.controller;

import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.service.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<Void> callback(@RequestParam Map<String, String> params) {
        // Lấy mã phản hồi và thông tin ticketId từ tham số
        String responseCode = params.get("vnp_ResponseCode");
        String orderId = params.get("vnp_TxnRef");

        // Xử lý thanh toán
        vnPayService.callBack(responseCode, orderId);

        // URL của frontend, kèm theo các tham số động
        String frontendUrl = "http://localhost:3000/paymentCallback";
        String redirectUrl = frontendUrl + "?";

        // Thêm tất cả các tham số vào URL redirect
        for (Map.Entry<String, String> entry : params.entrySet()) {
            redirectUrl += entry.getKey() + "=" + entry.getValue() + "&";
        }

        // Loại bỏ dấu "&" thừa cuối cùng
        redirectUrl = redirectUrl.substring(0, redirectUrl.length() - 1);

        // Redirect người dùng đến frontend với các tham số
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl)
                .build();
    }
}
