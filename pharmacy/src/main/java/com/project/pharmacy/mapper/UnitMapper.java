package com.project.pharmacy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.project.pharmacy.dto.request.UnitCreateRequest;
import com.project.pharmacy.dto.request.UnitUpdateRequest;
import com.project.pharmacy.dto.response.UnitResponse;
import com.project.pharmacy.entity.Unit;

@Mapper(componentModel = "spring")
public interface UnitMapper {
    Unit toUnit(UnitCreateRequest request);

    UnitResponse toUnitResponse(Unit unit);

    void updateUnit(@MappingTarget Unit unit, UnitUpdateRequest request);
}
