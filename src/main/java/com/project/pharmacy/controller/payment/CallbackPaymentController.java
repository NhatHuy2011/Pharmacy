package com.project.pharmacy.controller.payment;

import com.project.pharmacy.dto.request.payment.CallBackRequest;
import com.project.pharmacy.dto.response.common.ApiResponse;
import com.project.pharmacy.dto.response.entity.OrderResponse;
import com.project.pharmacy.service.payment.CallbackService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/callback")
public class CallbackPaymentController {
    CallbackService callbackService;

    @PostMapping
    public ApiResponse<OrderResponse> callBack(@RequestBody CallBackRequest request){
        return ApiResponse.<OrderResponse>builder()
                .result(callbackService.callBack(request))
                .build();
    }
}
