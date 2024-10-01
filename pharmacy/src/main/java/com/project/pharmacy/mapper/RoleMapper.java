package com.project.pharmacy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.project.pharmacy.dto.request.RoleCreateRequest;
import com.project.pharmacy.dto.request.RoleUpdateRequest;
import com.project.pharmacy.dto.response.RoleResponse;
import com.project.pharmacy.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleCreateRequest request);

    RoleResponse toRoleResponse(Role role);

    @Mapping(target = "permissions", ignore = true)
    void updateRole(@MappingTarget Role role, RoleUpdateRequest request);
}
