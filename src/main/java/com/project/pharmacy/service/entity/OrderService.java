package com.project.pharmacy.service.entity;

import com.project.pharmacy.dto.request.delivery.CalculateDeliveryFeeRequest;
import com.project.pharmacy.dto.request.delivery.CalculateExpectedDeliveryTimeRequest;
import com.project.pharmacy.dto.request.order.*;
import com.project.pharmacy.dto.response.delivery.CalculateDeliveryOrderFeeResponse;
import com.project.pharmacy.dto.response.delivery.CalcuteExpectedDeliveryTimeResponse;
import com.project.pharmacy.dto.response.delivery.DeliveryResponse;
import com.project.pharmacy.dto.response.entity.*;
import com.project.pharmacy.dto.response.payment.RefundPaymentResponse;
import com.project.pharmacy.enums.OrderStatus;
import com.project.pharmacy.enums.PaymentMethod;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.OrdersMapper;
import com.project.pharmacy.mapper.PriceMapper;
import com.project.pharmacy.repository.*;
import com.project.pharmacy.repository.httpclient.DeliveryClient;
import com.project.pharmacy.service.delivery.DeliveryService;
import com.project.pharmacy.service.email.EmailService;
import com.project.pharmacy.service.payment.VNPayService;
import com.project.pharmacy.utils.CartTemporary;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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

	PriceRepository priceRepository;

	CartRepository cartRepository;

	CartItemRepository cartItemRepository;

	AddressRepository addressRepository;

	ImageRepository imageRepository;

	CouponRepository couponRepository;

	DeliveryService deliveryService;

	DeliveryClient deliveryClient;

	VNPayService vnPayService;

	PriceMapper priceMapper;

	@NonFinal
	@Value("${ghn.district_id}")
	int districtId;

	@NonFinal
	@Value("${ghn.ward_code}")
	String wardCode;

	//For User
	//Cart User
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

		//Lấy/Tạo địa chỉ nhận hàng
		Address address = user.getAddresses().stream()
				.filter(address1 -> address1.getId().equals(request.getAddressId()))
				.findFirst()
				.orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

		//Tạo mã giảm giá
		Coupon coupon;
		int amountCoupon = 0;
		if (request.getCouponId() != null) {
			coupon = couponRepository.findById(request.getCouponId())
					.orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));

			if(coupon.getOrderRequire() > cart.getTotalPrice()){
				int amount = coupon.getOrderRequire() - cart.getTotalPrice();

				throw new AppException(ErrorCode.COUPON_DONT_MATCH_ORDERREQUIRE,
						String.format(ErrorCode.COUPON_DONT_MATCH_ORDERREQUIRE.getMessage(), amount));
			}

			amountCoupon = Math.min((coupon.getPercent() * cart.getTotalPrice()) / 100, coupon.getMax());
		}

		//Tạo đơn hàng
		Orders order = Orders.builder()
    			.user(user)
				.address(address)
    			.orderDate(LocalDateTime.now())
				.status(OrderStatus.PENDING)
				.orderItems(new ArrayList<>())
				.paymentMethod(request.getPaymentMethod())
				.totalPrice(cart.getTotalPrice())
				.coupon(amountCoupon)
				.isConfirm(false)
				.isReceived(false)
    			.build();

		//Tạo request để giao hàng
		CalculateDeliveryFeeRequest feeRequest = CalculateDeliveryFeeRequest.builder()
				.service_id(request.getService_id())
				.to_district_id(address.getDistrict())
				.to_ward_code(address.getVillage())
				.build();

		if(request.getIsInsurance()){
			feeRequest.setInsurance_value(cart.getTotalPrice());
		} else {
			feeRequest.setInsurance_value(0);
		}

		//Tính phí giao hàng
		DeliveryResponse<CalculateDeliveryOrderFeeResponse> feeResponse = deliveryService.calculateFeeDeliveryOrder(feeRequest);

		order.setDeliveryTotal(feeResponse.getData().getTotal());
		order.setServiceFee(feeResponse.getData().getServiceFee());
		order.setInsuranceFee(feeResponse.getData().getInsuranceFee());
		order.setNewTotalPrice(cart.getTotalPrice() - amountCoupon + feeResponse.getData().getTotal());

		//Tạo request để tính thời gian giao hàng
		CalculateExpectedDeliveryTimeRequest deliveryTimeRequest = CalculateExpectedDeliveryTimeRequest.builder()
				.from_district_id(districtId)
				.from_ward_code(wardCode)
				.to_district_id(address.getDistrict())
				.to_ward_code(address.getVillage())
				.service_id(request.getService_id())
				.build();

		//Tính thời gian nhận hàng
		DeliveryResponse<CalcuteExpectedDeliveryTimeResponse> deliveryTimeResponse = deliveryClient.getLeadTime(deliveryTimeRequest);

		order.setLeadTime(deliveryTimeResponse.getData().getLeadtime());
		orderRepository.save(order);

		//Lưu lại orderitem
		List<CartItem> cartItems = cart.getCartItems();
		List<OrderItem> orderItems = cartItems.stream()
						.map(cartItem -> {
							OrderItem orderItem = OrderItem.builder()
                                    .quantity(cartItem.getQuantity())
                                    .price(cartItem.getPrice())
									.amount(cartItem.getAmount())
                                    .orders(order)
									.image(cartItem.getImage())
                                    .build();
							orderItemRepository.save(orderItem);
							return orderItem;
						})
						.toList();
		order.setOrderItems(orderItems);

		//Response
		List<OrderItemResponse> orderItemResponses = order.getOrderItems().stream()
				.map(orderItem -> OrderItemResponse.builder()
                        .id(orderItem.getId())
						.productId(orderItem.getPrice().getProduct().getId())
                        .priceId(orderItem.getPrice().getId())
                        .productName(orderItem.getPrice().getProduct().getName())
                        .unitName(orderItem.getPrice().getUnit().getName())
                        .quantity(orderItem.getQuantity())
                        .price(orderItem.getPrice().getPrice())
						.amount(orderItem.getAmount())
						.image(orderItem.getImage())
                        .build())
				.toList();

		OrderResponse orderResponse = ordersMapper.toOrderResponse(order);
		orderResponse.setUserId(order.getUser().getId());
		orderResponse.setOrderItemResponses(orderItemResponses);

		//Xoá giỏ hàng
		cartItemRepository.deleteAll(cart.getCartItems());
		cart.setTotalPrice(0);
		cartRepository.save(cart);

		return orderResponse;
    }

	//Home User
	public OrderResponse createOrderAtHomeUser(CreateOrderRequestAtHomeUser request) throws AppException {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();

		User user = userRepository.findByUsername(name)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		if(user.getAddresses().isEmpty())
			throw new AppException(ErrorCode.UPDATE_ADDRESS);

		//Lấy/Tạo địa chỉ
		Address address = user.getAddresses().stream()
				.filter(address1 -> address1.getId().equals(request.getAddressId()))
				.findFirst()
				.orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

		//Lấy giá sản phẩm
		Price price = priceRepository.findById(request.getPriceId())
				.orElseThrow(() -> new AppException(ErrorCode.PRICE_NOT_FOUND));

		//Lấy hình ảnh
		Image firstImage = imageRepository.findFirstByProductId(price.getProduct().getId());
		String url = firstImage.getSource();

		//Tạo mã giảm giá
		Coupon coupon;
		int amountCoupon = 0;
		if (request.getCouponId() != null) {
			coupon = couponRepository.findById(request.getCouponId())
					.orElseThrow(() -> new AppException(ErrorCode.COUPON_NOT_FOUND));

			if (coupon.getOrderRequire() > price.getPrice()) {
				int amount = coupon.getOrderRequire() - price.getPrice();

				throw new AppException(ErrorCode.COUPON_DONT_MATCH_ORDERREQUIRE,
						String.format(ErrorCode.COUPON_DONT_MATCH_ORDERREQUIRE.getMessage(), amount));
			}

			amountCoupon = Math.min((coupon.getPercent() * price.getPrice()) / 100, coupon.getMax());
		}

		//Tạo đơn hàng
		Orders orders = Orders.builder()
				.user(user)
				.address(address)
				.orderDate(LocalDateTime.now())
				.status(OrderStatus.PENDING)
				.paymentMethod(request.getPaymentMethod())
				.isConfirm(false)
				.coupon(amountCoupon)
				.totalPrice(price.getPrice())
				.orderItems(new ArrayList<>())
				.isReceived(false)
				.build();

		//Tạo request để giao hàng
		CalculateDeliveryFeeRequest feeRequest = CalculateDeliveryFeeRequest.builder()
				.service_id(request.getService_id())
				.to_district_id(address.getDistrict())
				.to_ward_code(address.getVillage())
				.build();

		if(request.getIsInsurance()){
			feeRequest.setInsurance_value(price.getPrice());
		} else {
			feeRequest.setInsurance_value(0);
		}

		//Tính phí giao hàng
		DeliveryResponse<CalculateDeliveryOrderFeeResponse> feeResponse = deliveryService.calculateFeeDeliveryOrder(feeRequest);

		orders.setDeliveryTotal(feeResponse.getData().getTotal());
		orders.setServiceFee(feeResponse.getData().getServiceFee());
		orders.setInsuranceFee(feeResponse.getData().getInsuranceFee());
		orders.setNewTotalPrice(price.getPrice() - amountCoupon + feeResponse.getData().getTotal());

		//Tạo request để tính thời gian giao hàng
		CalculateExpectedDeliveryTimeRequest deliveryTimeRequest = CalculateExpectedDeliveryTimeRequest.builder()
				.from_district_id(districtId)
				.from_ward_code(wardCode)
				.to_district_id(address.getDistrict())
				.to_ward_code(address.getVillage())
				.service_id(request.getService_id())
				.build();

		//Tính thời gian nhận hàng
		DeliveryResponse<CalcuteExpectedDeliveryTimeResponse> deliveryTimeResponse = deliveryClient.getLeadTime(deliveryTimeRequest);

		orders.setLeadTime(deliveryTimeResponse.getData().getLeadtime());
		orderRepository.save(orders);

		OrderItem orderItem = OrderItem.builder()
				.orders(orders)
				.price(price)
				.quantity(1)
				.amount(price.getPrice())
				.image(url)
				.build();
		orderItemRepository.save(orderItem);
		orders.getOrderItems().add(orderItem);

		//Response
		List<OrderItemResponse> orderItemResponse = orders.getOrderItems().stream()
						.map(orderItem1 -> {
							return OrderItemResponse.builder()
									.id(orderItem.getId())
									.productId(orderItem.getPrice().getProduct().getId())
									.productName(orderItem.getPrice().getProduct().getName())
									.unitName(orderItem.getPrice().getUnit().getName())
									.priceId(orderItem.getPrice().getId())
									.quantity(orderItem.getQuantity())
									.price(orderItem.getPrice().getPrice())
									.amount(orderItem.getAmount())
									.image(orderItem.getImage())
									.build();
						})
				.collect(Collectors.toList());

		OrderResponse orderResponse = ordersMapper.toOrderResponse(orders);
		orderResponse.setUserId(orders.getUser().getId());
		orderResponse.setOrderItemResponses(orderItemResponse);

		return orderResponse;
	}

	//Xem lich su don hang cua User
	public List<OrderResponse> getOrderByUser(){
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();

		User user = userRepository.findByUsername(name)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

		return user.getOrders().stream()
				.map(orders -> {
					OrderResponse orderResponse = ordersMapper.toOrderResponse(orders);

					List<OrderItemResponse> orderItemResponses = orders.getOrderItems().stream()
							.map(orderItem -> {
								return OrderItemResponse.builder()
										.id(orderItem.getId())
										.productId(orderItem.getPrice().getProduct().getId())
										.productName(orderItem.getPrice().getProduct().getName())
										.unitName(orderItem.getPrice().getUnit().getName())
										.priceId(orderItem.getPrice().getId())
										.quantity(orderItem.getQuantity())
										.price(orderItem.getPrice().getPrice())
										.amount(orderItem.getAmount())
										.image(orderItem.getImage())
										.build();
							})
							.toList();

					orderResponse.setOrderItemResponses(orderItemResponses);
					orderResponse.setUserId(orders.getUser().getId());

					return orderResponse;
				})
				.toList();
	}

	@PreAuthorize("hasRole('USER')")
	public void receiverOrderUser(String orderId){
		Orders orders = orderRepository.findById(orderId)
				.orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
		orders.setIsReceived(true);
		orderRepository.save(orders);
	}

	//For Guest
	//Cart Guest
	public OrderResponse createOrderAtCartGuest(CreateOrderRequestAtCartGuest request, HttpSession session){
		CartTemporary cartTemporary = (CartTemporary) session.getAttribute("Cart");

		if(cartTemporary == null || cartTemporary.getCartItemResponses().isEmpty())
			throw new AppException(ErrorCode.CART_EMPTY);

		//Tạo địa chỉ nhận hàng
		Address address = Address.builder()
				.user(null)
				.fullname(request.getFullname())
				.phone(request.getPhone())
				.province(request.getProvince())
				.district(request.getDistrict())
				.village(request.getVillage())
				.address(request.getAddress())
				.addressCategory(request.getAddressCategory())
				.build();
		
		addressRepository.save(address);

		//Tạo đơn hàng
		Orders orders = Orders.builder()
				.user(null)
				.orderDate(LocalDateTime.now())
				.status(OrderStatus.PENDING)
				.paymentMethod(request.getPaymentMethod())
				.address(address)
				.totalPrice(cartTemporary.getTotalPrice())
				.isConfirm(false)
				.isReceived(false)
				.email(request.getEmail())
				.build();

		//Tạo request để giao hàng
		CalculateDeliveryFeeRequest feeRequest = CalculateDeliveryFeeRequest.builder()
				.service_id(request.getService_id())
				.to_district_id(request.getDistrict())
				.to_ward_code(request.getVillage())
				.build();

		if(request.getIsInsurance()){
			feeRequest.setInsurance_value(cartTemporary.getTotalPrice());
		} else {
			feeRequest.setInsurance_value(0);
		}

		//Tính phí giao hàng
		DeliveryResponse<CalculateDeliveryOrderFeeResponse> feeResponse = deliveryService.calculateFeeDeliveryOrder(feeRequest);

		orders.setDeliveryTotal(feeResponse.getData().getTotal());
		orders.setServiceFee(feeResponse.getData().getServiceFee());
		orders.setInsuranceFee(feeResponse.getData().getInsuranceFee());
		orders.setNewTotalPrice(cartTemporary.getTotalPrice() + feeResponse.getData().getTotal());

		//Tạo request để tính thời gian giao hàng
		CalculateExpectedDeliveryTimeRequest deliveryTimeRequest = CalculateExpectedDeliveryTimeRequest.builder()
				.from_district_id(districtId)
				.from_ward_code(wardCode)
				.to_district_id(address.getDistrict())
				.to_ward_code(address.getVillage())
				.service_id(request.getService_id())
				.build();

		//Tính thời gian nhận hàng
		DeliveryResponse<CalcuteExpectedDeliveryTimeResponse> deliveryTimeResponse = deliveryClient.getLeadTime(deliveryTimeRequest);
		orders.setLeadTime(deliveryTimeResponse.getData().getLeadtime());

		orderRepository.save(orders);

		//Tạo orderitem
		List<OrderItem> orderItems = cartTemporary.getCartItemResponses().stream()
				.map(cartItemTemporary -> {
					OrderItem orderItem = OrderItem.builder()
							.price(priceRepository.findById(cartItemTemporary.getPriceId())
									.orElseThrow(() -> new AppException(ErrorCode.PRICE_NOT_FOUND)))
							.orders(orders)
							.quantity(cartItemTemporary.getQuantity())
							.amount(cartItemTemporary.getAmount())
							.image(cartItemTemporary.getImage())
							.build();

					orderItemRepository.save(orderItem);

					return orderItem;
				})
				.toList();
		orders.setOrderItems(orderItems);

		//Response
		List<OrderItemResponse> orderItemResponses = orders.getOrderItems().stream()
				.map(orderItem -> OrderItemResponse.builder()
						.id(orderItem.getId())
						.productId(orderItem.getPrice().getProduct().getId())
						.priceId(orderItem.getPrice().getId())
						.productName(orderItem.getPrice().getProduct().getName())
						.unitName(orderItem.getPrice().getUnit().getName())
						.quantity(orderItem.getQuantity())
						.price(orderItem.getPrice().getPrice())
						.amount(orderItem.getAmount())
						.image(orderItem.getImage())
						.build())
				.toList();

		OrderResponse orderResponse = ordersMapper.toOrderResponse(orders);
		orderResponse.setOrderItemResponses(orderItemResponses);

		//Xoá giỏ hàng
		cartTemporary.getCartItemResponses().clear();
		cartTemporary.setTotalPrice(0);

		return orderResponse;
	}

	//Home Guest
		public OrderResponse createOrderAtHomeGuest(CreateOrderRequestAtHomeGuest request, HttpSession session){
		Price price = priceRepository.findById(request.getPriceId())
				.orElseThrow(() -> new AppException(ErrorCode.PRICE_NOT_FOUND));

		Image firstImage = imageRepository.findFirstByProductId(price.getProduct().getId());
		String url = firstImage.getSource();

		//Tạo địa chỉ nhận hàng
		Address address = Address.builder()
				.user(null)
				.fullname(request.getFullname())
				.phone(request.getPhone())
				.province(request.getProvince())
				.district(request.getDistrict())
				.village(request.getVillage())
				.address(request.getAddress())
				.addressCategory(request.getAddressCategory())
				.build();
		addressRepository.save(address);

		//Tạo đơn hàng
		Orders orders = Orders.builder()
				.user(null)
				.orderDate(LocalDateTime.now())
				.status(OrderStatus.PENDING)
				.paymentMethod(request.getPaymentMethod())
				.address(address)
				.isConfirm(false)
				.totalPrice(price.getPrice())
				.orderItems(new ArrayList<>())
				.isReceived(false)
				.email(request.getEmail())
				.build();

		//Tạo request để giao hàng
		CalculateDeliveryFeeRequest feeRequest = CalculateDeliveryFeeRequest.builder()
				.service_id(request.getService_id())
				.to_district_id(address.getDistrict())
				.to_ward_code(address.getVillage())
				.build();

		if(request.getIsInsurance()){
			feeRequest.setInsurance_value(price.getPrice());
		} else {
			feeRequest.setInsurance_value(0);
		}

		//Tính phí giao hàng
		DeliveryResponse<CalculateDeliveryOrderFeeResponse> feeResponse = deliveryService.calculateFeeDeliveryOrder(feeRequest);
		orders.setDeliveryTotal(feeResponse.getData().getTotal());
		orders.setServiceFee(feeResponse.getData().getServiceFee());
		orders.setInsuranceFee(feeResponse.getData().getInsuranceFee());
		orders.setNewTotalPrice(price.getPrice() + feeResponse.getData().getTotal());

		//Tạo request để tính thời gian giao hàng
		CalculateExpectedDeliveryTimeRequest deliveryTimeRequest = CalculateExpectedDeliveryTimeRequest.builder()
				.from_district_id(districtId)
				.from_ward_code(wardCode)
				.to_district_id(address.getDistrict())
				.to_ward_code(address.getVillage())
				.service_id(request.getService_id())
				.build();

		//Tính thời gian nhận hàng
		DeliveryResponse<CalcuteExpectedDeliveryTimeResponse> deliveryTimeResponse = deliveryClient.getLeadTime(deliveryTimeRequest);
		orders.setLeadTime(deliveryTimeResponse.getData().getLeadtime());

		orderRepository.save(orders);

		//Tạo orderitem
		OrderItem orderItem = OrderItem.builder()
				.orders(orders)
				.price(price)
				.quantity(1)
				.amount(price.getPrice())
				.image(url)
				.build();
		orderItemRepository.save(orderItem);
		orders.getOrderItems().add(orderItem);

		//Response
		List<OrderItemResponse> orderItemResponse = orders.getOrderItems().stream()
				.map(orderItem1 -> {
					return OrderItemResponse.builder()
							.id(orderItem.getId())
							.productId(orderItem.getPrice().getProduct().getId())
							.productName(orderItem.getPrice().getProduct().getName())
							.unitName(orderItem.getPrice().getUnit().getName())
							.priceId(orderItem.getPrice().getId())
							.quantity(orderItem.getQuantity())
							.price(orderItem.getPrice().getPrice())
							.amount(orderItem.getAmount())
							.image(orderItem.getImage())
							.build();
				})
				.collect(Collectors.toList());

		OrderResponse orderResponse = ordersMapper.toOrderResponse(orders);
		orderResponse.setOrderItemResponses(orderItemResponse);

		return orderResponse;
	}

	//FOR EMPLOYEE AND ADMIN
	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	public Page<OrderResponse> getAllByStatus(Pageable pageable){
		Page<Orders> ordersPage = orderRepository.findByStatus(OrderStatus.SUCCESS, pageable);

		return ordersPage.map(orders -> {
					OrderResponse orderResponse = ordersMapper.toOrderResponse(orders);

					List<OrderItemResponse> orderItemResponses = orders.getOrderItems().stream()
							.map(orderItem -> {
								return OrderItemResponse.builder()
										.id(orderItem.getId())
										.productId(orderItem.getPrice().getProduct().getId())
										.productName(orderItem.getPrice().getProduct().getName())
										.unitName(orderItem.getPrice().getUnit().getName())
										.priceId(orderItem.getPrice().getId())
										.quantity(orderItem.getQuantity())
										.price(orderItem.getPrice().getPrice())
										.amount(orderItem.getAmount())
										.image(orderItem.getImage())
										.build();
							})
							.toList();

					orderResponse.setOrderItemResponses(orderItemResponses);
					orderResponse.setUserId(orders.getUser() != null ? orders.getUser().getId() : null);

					return orderResponse;
				});
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	public Page<OrderResponse> getAllOrderCOD(Pageable pageable){
		Page<Orders> ordersPage = orderRepository.findByPaymentMethodAndStatus(PaymentMethod.CASH, OrderStatus.PENDING, pageable);

		return ordersPage.map(orders -> {
			OrderResponse orderResponse = ordersMapper.toOrderResponse(orders);

			List<OrderItemResponse> orderItemResponses = orders.getOrderItems().stream()
					.map(orderItem -> {
						return OrderItemResponse.builder()
								.id(orderItem.getId())
								.productId(orderItem.getPrice().getProduct().getId())
								.productName(orderItem.getPrice().getProduct().getName())
								.unitName(orderItem.getPrice().getUnit().getName())
								.priceId(orderItem.getPrice().getId())
								.quantity(orderItem.getQuantity())
								.price(orderItem.getPrice().getPrice())
								.amount(orderItem.getAmount())
								.image(orderItem.getImage())
								.build();
					})
					.toList();

			orderResponse.setOrderItemResponses(orderItemResponses);
			orderResponse.setUserId(orders.getUser() != null ? orders.getUser().getId() : null);

			return orderResponse;
		});
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
	public List<PriceResponse> confirmOrders(String orderId){
		Orders orders = orderRepository.findById(orderId)
				.orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

		List<PriceResponse> priceResponses = new ArrayList<>();

		orders.getOrderItems()
				.forEach(orderItem -> {
					Price price = priceRepository.findById(orderItem.getPrice().getId())
							.orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

					if(price.getQuantity() - orderItem.getQuantity() < 0) {
						throw new AppException(ErrorCode.PRICE_OVER_QUANTITY);
					} else {
						price.setQuantity(price.getQuantity() - orderItem.getQuantity());
					}

					PriceResponse priceResponse =  priceMapper.toPriceResponse(price);
					priceResponse.setProduct(ProductResponse.builder()
							.id(price.getProduct().getId())
							.name(price.getProduct().getName())
							.build());
					priceResponse.setUnit(UnitResponse.builder()
							.id(price.getUnit().getId())
							.name(price.getUnit().getName())
							.build());

					priceResponses.add(priceResponse);
				});

		orders.setIsConfirm(true);
		orderRepository.save(orders);

		return priceResponses;
	}

	//FOR NURSE
	@PreAuthorize("hasRole('NURSE')")
	public OrderResponse createOrderAtShop(CreateOrderAtShopRequest request){
		User user = userRepository.findByPhoneNumber(request.getPhone())
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

		Orders orders = Orders.builder()
				.user(user)
				.address(null)
				.orderDate(LocalDateTime.now())
				.status(OrderStatus.PENDING)
				.paymentMethod(request.getPaymentMethod())
				.isConfirm(true)
				.isReceived(true)
				.build();
		orderRepository.save(orders);

		List<OrderItem> orderItems = new ArrayList<>();
		int totalPrice = 0;
		for(PriceDTO priceDTO : request.getListPrices()){
			Price price = priceRepository.findById(priceDTO.getId())
					.orElseThrow(() -> new AppException(ErrorCode.PRICE_NOT_FOUND));
			totalPrice += price.getPrice()*priceDTO.getQuantity();
			OrderItem orderItem = OrderItem.builder()
					.orders(orders)
					.price(price)
					.quantity(priceDTO.getQuantity())
					.amount(price.getPrice()*priceDTO.getQuantity())
					.image(price.getProduct().getImages().stream().findFirst()
							.map(Image::getSource)
							.orElse(null))
					.build();
			orderItemRepository.save(orderItem);
			orderItems.add(orderItem);
		}

		orders.setNewTotalPrice(totalPrice);
		orderRepository.save(orders);

		List<OrderItemResponse> orderItemResponses = orderItems.stream()
				.map(orderItem -> {
					return OrderItemResponse.builder()
							.id(orderItem.getId())
							.productId(orderItem.getPrice().getProduct().getId())
							.productName(orderItem.getPrice().getProduct().getName())
							.unitName(orderItem.getPrice().getUnit().getName())
							.priceId(orderItem.getPrice().getId())
							.quantity(orderItem.getQuantity())
							.price(orderItem.getPrice().getPrice())
							.amount(orderItem.getAmount())
							.image(orderItem.getImage())
							.build();
				})
				.toList();
		OrderResponse orderResponse = ordersMapper.toOrderResponse(orders);
		orderResponse.setUserId(user.getId());
		orderResponse.setOrderItemResponses(orderItemResponses);

		return orderResponse;
	}

	//FOR ALL (FOLLOW ORDER)
	public OrderResponse getOrderDetails(String id){
		Orders orders = orderRepository.findById(id)
				.orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

		OrderResponse orderResponse = ordersMapper.toOrderResponse(orders);

		List<OrderItemResponse> orderItemResponses = orders.getOrderItems().stream()
				.map(orderItem -> {
					return OrderItemResponse.builder()
							.id(orderItem.getId())
							.productId(orderItem.getPrice().getProduct().getId())
							.productName(orderItem.getPrice().getProduct().getName())
							.unitName(orderItem.getPrice().getUnit().getName())
							.priceId(orderItem.getPrice().getId())
							.quantity(orderItem.getQuantity())
							.price(orderItem.getPrice().getPrice())
							.amount(orderItem.getAmount())
							.image(orderItem.getImage())
							.build();
				})
				.toList();

		orderResponse.setOrderItemResponses(orderItemResponses);
		orderResponse.setUserId(orders.getUser() != null ? orders.getUser().getId() : null);

		return orderResponse;
	}

	public RefundPaymentResponse cancelOrders(HttpServletRequest request){
		Orders orders = orderRepository.findById(request.getParameter("orderId"))
				.orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

		RefundPaymentResponse response = new RefundPaymentResponse();

		if(orders.getIsConfirm()){
			response.setIsSuccess(false);
		} else{
			if(orders.getStatus()==OrderStatus.SUCCESS){
				response = vnPayService.refundVNPay(request);
				response.setIsSuccess(true);
				orders.setStatus(OrderStatus.CANCELLED);
				orders.setIsConfirm(false);
				orders.setIsReceived(false);
				orderRepository.save(orders);
			} else {
				orders.setStatus(OrderStatus.CANCELLED);
				orders.setIsConfirm(false);
				orders.setIsReceived(false);
				orderRepository.save(orders);
				response.setIsSuccess(true);
			}
		}

		return response;
	}
}

