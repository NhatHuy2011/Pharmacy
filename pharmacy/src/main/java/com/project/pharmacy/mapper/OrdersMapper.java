package com.project.pharmacy.mapper;

import com.project.pharmacy.dto.request.CreateOrderRequestAtCartGuest;
import com.project.pharmacy.dto.request.CreateOrderRequestAtHomeGuest;
import com.project.pharmacy.dto.response.OrderResponse;
import com.project.pharmacy.entity.Orders;
import com.project.pharmacy.utils.OrderTemporary;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrdersMapper {
    OrderResponse toOrderResponse(Orders orders);

    OrderTemporary toOrderTemporaryCart(CreateOrderRequestAtCartGuest request);

    OrderTemporary toOrderTemporaryHome(CreateOrderRequestAtHomeGuest request);
}
