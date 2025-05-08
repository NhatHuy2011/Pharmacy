package com.project.pharmacy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.project.pharmacy.dto.request.role.RoleCreateRequest;
import com.project.pharmacy.dto.request.role.RoleUpdateRequest;
import com.project.pharmacy.dto.response.entity.RoleResponse;
import com.project.pharmacy.entity.Role;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {
    Role toRole(RoleCreateRequest request);

    RoleResponse toRoleResponse(Role role);

    void updateRole(@MappingTarget Role role, RoleUpdateRequest request);
}
