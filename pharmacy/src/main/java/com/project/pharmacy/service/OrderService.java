package com.project.pharmacy.service;

import com.project.pharmacy.dto.request.CreateOrderRequestAtCartGuest;
import com.project.pharmacy.dto.request.CreateOrderRequestAtCartUser;
import com.project.pharmacy.dto.request.CreateOrderRequestAtHomeGuest;
import com.project.pharmacy.dto.request.CreateOrderRequestAtHomeUser;
import com.project.pharmacy.dto.response.OrderItemResponse;
import com.project.pharmacy.dto.response.OrderResponse;
import com.project.pharmacy.enums.OrderStatus;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.OrdersMapper;
import com.project.pharmacy.repository.*;
import com.project.pharmacy.utils.CartTemporary;
import com.project.pharmacy.utils.OrderItemTemporary;
import com.project.pharmacy.utils.OrderTemporary;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.project.pharmacy.entity.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;

	OrderItemRepository orderItemRepository;

    UserRepository userRepository;

	OrdersMapper ordersMapper;

	ProductRepository productRepository;

	UnitRepository unitRepository;

	PriceRepository priceRepository;

	CartRepository cartRepository;

	CartItemRepository cartItemRepository;

	@Transactional
	public OrderResponse createOrderAtCartUser(CreateOrderRequestAtCartUser request){
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

		Address address = user.getAddresses().stream()
				.filter(address1 -> address1.getId().equals(request.getAddressId()))
				.findFirst()
				.orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

    	Orders order = Orders.builder()
    			.user(user)
				.address(address)
    			.orderDate(LocalDateTime.now())
				.status(OrderStatus.PENDING)
				.orderItems(new ArrayList<>())
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

		order.setOrderItems(orderItems);

		cartItemRepository.deleteAll(cart.getCartItems());

		//cartItemRepository.deleteAllByCartId(cart.getId());
		cart.setTotalPrice(0);
		cartRepository.save(cart);

		List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
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

	public OrderResponse createOrderAtHomeUser(CreateOrderRequestAtHomeUser request){
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();

		User user = userRepository.findByUsername(name)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		if(user.getAddresses().isEmpty())
			throw new AppException(ErrorCode.UPDATE_ADDRESS);

		Address address = user.getAddresses().stream()
				.filter(address1 -> address1.getId().equals(request.getAddressId()))
				.findFirst()
				.orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

		Unit unit = unitRepository.findById(request.getUnitId())
				.orElseThrow(() -> new AppException(ErrorCode.UNIT_NOT_FOUND));

		Price price = priceRepository.findByProductAndUnit(product, unit);

		Orders orders = Orders.builder()
				.user(user)
				.address(address)
				.orderDate(LocalDateTime.now())
				.status(OrderStatus.PENDING)
				.paymentMethod(request.getPaymentMethod())
				.totalPrice(price.getPrice())
				.orderItems(new ArrayList<>())
				.build();
		orderRepository.save(orders);

		OrderItem orderItem = OrderItem.builder()
				.orders(orders)
				.price(price)
				.quantity(1)
				.build();
		orderItemRepository.save(orderItem);

		orders.getOrderItems().add(orderItem);

		List<OrderItemResponse> orderItemResponse = orders.getOrderItems().stream()
						.map(orderItem1 -> {
							return OrderItemResponse.builder()
									.id(orderItem.getId())
									.productName(orderItem.getPrice().getProduct().getName())
									.unitName(orderItem.getPrice().getUnit().getName())
									.priceId(orderItem.getPrice().getId())
									.quantity(orderItem.getQuantity())
									.price(orderItem.getPrice().getPrice())
									.build();
						})
				.collect(Collectors.toList());

		OrderResponse orderResponse = ordersMapper.toOrderResponse(orders);
		orderResponse.setOrderItemResponses(orderItemResponse);

		return orderResponse;
	}

	public OrderTemporary createOrderAtCartGuest(CreateOrderRequestAtCartGuest request, HttpSession session){
		CartTemporary cartTemporary = (CartTemporary) session.getAttribute("Cart");

		if(cartTemporary == null || cartTemporary.getCartItems().isEmpty())
			throw new AppException(ErrorCode.CART_EMPTY);

		List<OrderItemTemporary> orderItemTemporaries = cartTemporary.getCartItems().stream()
				.map(cartItemTemporary -> {
                    return OrderItemTemporary.builder()
                            .id(UUID.randomUUID().toString())
                            .priceId(cartItemTemporary.getPriceId())
                            .productName(cartItemTemporary.getProductName())
                            .unitName(cartItemTemporary.getUnitName())
                            .quantity(cartItemTemporary.getQuantity())
                            .price(cartItemTemporary.getPrice())
                            .build();
				})
				.toList();

		OrderTemporary orderTemporary = ordersMapper.toOrderTemporaryCart(request);
		orderTemporary.setId(UUID.randomUUID().toString());
		orderTemporary.setOrderItemTemporaries(orderItemTemporaries);
		orderTemporary.setOrderDate(LocalDateTime.now());
		orderTemporary.setOrderStatus(OrderStatus.PENDING);
		orderTemporary.setTotalPrice(cartTemporary.getTotalPrice());

		session.setAttribute("Order", orderTemporary);

		cartTemporary.getCartItems().clear();
		cartTemporary.setTotalPrice(0);

		return orderTemporary;
	}

	public OrderTemporary createOrderAtHomeGuest(CreateOrderRequestAtHomeGuest request, HttpSession session){
		Product product = productRepository.findById(request.getProductId())
				.orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

		Unit unit = unitRepository.findById(request.getUnitId())
				.orElseThrow(() -> new AppException(ErrorCode.UNIT_NOT_FOUND));

		Price price = priceRepository.findByProductAndUnit(product, unit);

		OrderItemTemporary orderItemTemporary = OrderItemTemporary.builder()
				.id(UUID.randomUUID().toString())
				.productName(product.getName())
				.unitName(unit.getName())
				.priceId(price.getId())
				.quantity(1)
				.price(price.getPrice())
				.build();

		OrderTemporary orderTemporary = ordersMapper.toOrderTemporaryHome(request);
		orderTemporary.setId(UUID.randomUUID().toString());
		orderTemporary.setOrderItemTemporaries(new ArrayList<>());
		orderTemporary.setOrderDate(LocalDateTime.now());
		orderTemporary.setOrderStatus(OrderStatus.PENDING);
		orderTemporary.setTotalPrice(price.getPrice());
		orderTemporary.getOrderItemTemporaries().add(orderItemTemporary);

		session.setAttribute("Order", orderTemporary);

		return orderTemporary;
	}
}

