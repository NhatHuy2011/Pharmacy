package com.project.pharmacy.service;
import com.project.pharmacy.configuration.VNPayConfig;
import com.project.pharmacy.dto.request.OrderRequest;
import com.project.pharmacy.dto.response.OrderItemResponse;
import com.project.pharmacy.dto.response.OrderResponse;
import com.project.pharmacy.enums.OrderStatus;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.OrdersMapper;
import com.project.pharmacy.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.project.pharmacy.entity.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;

	OrderItemRepository orderItemRepository;

    UserRepository userRepository;

	OrdersMapper ordersMapper;

	AddressRepository addressRepository;

	public OrderResponse createOrderAtCartUser(OrderRequest request){
    	var context = SecurityContextHolder.getContext();
    	String name = context.getAuthentication().getName();

    	User user = userRepository.findByUsername(name)
    			.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

    	Cart cart = user.getCart();
    	if(cart == null || cart.getCartItems().isEmpty()){
    		throw new AppException(ErrorCode.CART_EMPTY);
    	}

    	if(user.getAddresses().isEmpty()){
    		throw new AppException(ErrorCode.UPDATE_ADDRESS);
    	}

		Address address = addressRepository.findById(request.getAddressId())
				.orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

    	Orders order = Orders.builder()
    			.user(user)
				.address(address)
    			.orderDate(LocalDateTime.now())
				.status(OrderStatus.PENDING)
				.paymentMethod(request.getPaymentMethod())
				.totalPrice(cart.getTotalPrice())
    			.build();
		orderRepository.save(order);

		List<CartItem> cartItems = cart.getCartItems();
		List<OrderItem> orderItems = cartItems.stream()
						.map(cartItem -> {
							OrderItem orderItem = OrderItem.builder()
                                    .quantity(cartItem.getQuantity())
                                    .price(cartItem.getPrice())
                                    .orders(order)
                                    .build();
							orderItemRepository.save(orderItem);
							return orderItem;
						})
						.toList();

		List<OrderItemResponse> orderItemResponses = orderItems.stream()
				.map(orderItem -> OrderItemResponse.builder()
                        .id(orderItem.getId())
                        .priceId(orderItem.getPrice().getId())
                        .productName(orderItem.getPrice().getProduct().getName())
                        .unitName(orderItem.getPrice().getUnit().getName())
                        .quantity(orderItem.getQuantity())
                        .price(orderItem.getPrice().getPrice())
                        .build())
				.toList();

		OrderResponse orderResponse = ordersMapper.toOrderResponse(order);
		orderResponse.setUserId(order.getUser().getId());
		orderResponse.setOrderItemResponses(orderItemResponses);

		return orderResponse;
    }


}

