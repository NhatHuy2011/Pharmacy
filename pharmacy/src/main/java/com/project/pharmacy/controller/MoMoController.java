package com.project.pharmacy.controller;

import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.service.MoMoService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/momo")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MoMoController {
    MoMoService moMoService;

    @PostMapping("/create-payment")
    public ApiResponse<Map<String, Object>> createPaymentWithMoMo(@RequestParam String orderId) throws IOException {
        return ApiResponse.<Map<String, Object>>builder()
                .result(moMoService.createPaymentMoMo(orderId))
                .build();
    }

    @GetMapping("/callback")
    public ResponseEntity<Void> callback(@RequestParam Map<String, String> params) {
        // Lấy mã phản hồi và thông tin orderId từ tham số
        String errorCode = params.get("errorCode");
        String orderId = params.get("orderId");

        // Xử lý thanh toán
        moMoService.callBack(Integer.parseInt(errorCode), orderId);

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
