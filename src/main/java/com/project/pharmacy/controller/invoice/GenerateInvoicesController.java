package com.project.pharmacy.controller.invoice;

import com.project.pharmacy.dto.response.common.ApiResponse;
import com.project.pharmacy.service.invoice.GenerateInvoiceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/invoice")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenerateInvoicesController {
    GenerateInvoiceService generateInvoiceService;

    @PostMapping
    public ApiResponse<Void> generateInvoice() throws IOException {
        generateInvoiceService.generateInvoices();
        return ApiResponse.<Void>builder()
                .message("Generate Invoice Successful")
                .build();
    }
}
