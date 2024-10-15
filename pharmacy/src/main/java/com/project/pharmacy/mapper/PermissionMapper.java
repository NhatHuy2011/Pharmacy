package com.project.pharmacy.mapper;

import org.mapstruct.Mapper;

import com.project.pharmacy.dto.request.PermissionCreateRequest;
import com.project.pharmacy.dto.response.PermissionResponse;
import com.project.pharmacy.entity.Permission;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PermissionMapper {
    Permission toPermission(PermissionCreateRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
