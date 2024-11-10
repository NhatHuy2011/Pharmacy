package com.project.pharmacy.mapper;

import com.project.pharmacy.dto.response.OrderResponse;
import com.project.pharmacy.entity.Orders;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrdersMapper {
    OrderResponse toOrderResponse(Orders orders);
}
