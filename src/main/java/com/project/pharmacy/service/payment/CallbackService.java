package com.project.pharmacy.service.payment;

import com.project.pharmacy.dto.request.payment.CallBackRequest;
import com.project.pharmacy.dto.response.entity.OrderResponse;
import com.project.pharmacy.entity.Orders;
import com.project.pharmacy.entity.User;
import com.project.pharmacy.enums.Level;
import com.project.pharmacy.enums.OrderStatus;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.OrdersMapper;
import com.project.pharmacy.repository.OrderRepository;
import com.project.pharmacy.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CallbackService {
    OrderRepository orderRepository;

    UserRepository userRepository;

    OrdersMapper ordersMapper;

    public OrderResponse callBack(CallBackRequest request){
        Orders orders = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        User user = orders.getUser();

        if (user == null){
            if(request.getCode() == 0 | request.getCode() == 1){
                orders.setStatus(OrderStatus.SUCCESS);
            } else {
                orders.setStatus(OrderStatus.FAILED);
            }
            orderRepository.save(orders);

            if (orders.getStatus() == OrderStatus.FAILED) {
                throw new AppException(ErrorCode.PAYMENT_ERRROR);
            }
        }
        else {
            if(request.getCode() == 0 | request.getCode() == 1){
                orders.setStatus(OrderStatus.SUCCESS);
                user.setPoint(user.getPoint() + orders.getTotalPrice()/1000);
                if (user.getPoint() >= 8000){
                    user.setLevel(Level.KIMCUONG);
                }
                else {
                    if (user.getPoint() >= 6000){
                        user.setLevel(Level.BACHKIM);
                    }
                    else if (user.getPoint() >= 4000){
                        user.setLevel(Level.VANG);
                    }
                    else if (user.getPoint() >= 2000) {
                        user.setLevel(Level.BAC);
                    }
                    else user.setLevel(Level.DONG);
                }
                userRepository.save(user);
            }
            else {
                orders.setStatus(OrderStatus.FAILED);
            }
            orderRepository.save(orders);

            if (orders.getStatus() == OrderStatus.FAILED) {
                throw new AppException(ErrorCode.PAYMENT_ERRROR);
            }
        }
        OrderResponse orderResponse = ordersMapper.toOrderResponse(orders);
        if (user != null) {
            orderResponse.setUserId(user.getId());
        } else {
            orderResponse.setUserId(null);
        }

        return orderResponse;
    }
}
