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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

        // URL của frontend
        String frontendUrl = "http://localhost:3000/paymentCallback";
        StringBuilder redirectUrl = new StringBuilder(frontendUrl + "?");

        // Encode các tham số để đảm bảo không có ký tự Unicode bất hợp lệ
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String encodedKey = URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8);
            String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8);
            redirectUrl.append(encodedKey).append("=").append(encodedValue).append("&");
        }

        // Loại bỏ dấu "&" thừa cuối cùng
        redirectUrl.setLength(redirectUrl.length() - 1);

        // Redirect người dùng đến frontend với các tham số
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, redirectUrl.toString())
                .build();
    }
}
