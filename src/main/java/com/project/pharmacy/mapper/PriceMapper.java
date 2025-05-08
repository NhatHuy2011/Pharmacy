package com.project.pharmacy.mapper;

import com.project.pharmacy.dto.response.entity.PriceResponse;
import com.project.pharmacy.entity.Price;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PriceMapper {
    @Mapping(target = "product", ignore = true)
    PriceResponse toPriceResponse(Price price);
}
