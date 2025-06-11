package com.project.pharmacy.dto.response.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    String id;
    String userId;
    AddressResponse address;
    List<OrderItemResponse> orderItemResponses;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yy HH:mm:ss")
    LocalDateTime orderDate;
    int totalPrice;
    String paymentMethod;
    String status;
    Boolean isConfirm;
    int deliveryTotal;
    int serviceFee;
    int insuranceFee;
    int coupon;
    int newTotalPrice;
    Long leadTime;
    String linkOrder;
    Boolean isReceived;
}
