package com.project.pharmacy.controller;

import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.service.MoMoService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
    public ApiResponse<Void> callback(@RequestParam(value = "errorCode") int errorCode,
                                      @RequestParam(value = "orderId") String orderId) {
        String message;
        if (errorCode == 0){
            message = "Thanh toán thành công";
        }else {
            message = "Thanh toán thất bại";
        }
        moMoService.callBack(errorCode, orderId);
        return ApiResponse.<Void>builder()
                .message(message)
                .build();
    }
}
