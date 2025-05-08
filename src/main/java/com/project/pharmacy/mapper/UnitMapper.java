package com.project.pharmacy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.project.pharmacy.dto.request.unit.UnitCreateRequest;
import com.project.pharmacy.dto.request.unit.UnitUpdateRequest;
import com.project.pharmacy.dto.response.entity.UnitResponse;
import com.project.pharmacy.entity.Unit;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UnitMapper {
    Unit toUnit(UnitCreateRequest request);

    UnitResponse toUnitResponse(Unit unit);

    void updateUnit(@MappingTarget Unit unit, UnitUpdateRequest request);
}
