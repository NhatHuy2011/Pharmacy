package com.project.pharmacy.dto.response.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefundPaymentResponse {
    String vnp_ResponseId;
    String vnp_Command;
    String vnp_ResponseCode;
    String vnp_Message;
    String vnp_TmnCode;
    String vnp_TxnRef;
    String vnp_Amount;
    String vnp_OrderInfo;
    String vnp_BankCode;
    String vnp_PayDate;
    String vnp_TransactionNo;
    String vnp_TransactionType;
    String vnp_TransactionStatus;
    String vnp_SecureHash;
    Boolean isSuccess;
}
