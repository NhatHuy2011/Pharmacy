package com.project.pharmacy.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.pharmacy.enums.AddressCategory;
import com.project.pharmacy.enums.OrderStatus;
import com.project.pharmacy.enums.PaymentMethod;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderTemporary {
    String id;
    String fullname;
    int phone;
    String province;
    String district;
    String village;
    String address;
    AddressCategory addressCategory;
    List<OrderItemTemporary> orderItemResponses;
    LocalDateTime orderDate;
    PaymentMethod paymentMethod;
    OrderStatus status;
    Boolean isConfirm;
    int totalPrice;
}
