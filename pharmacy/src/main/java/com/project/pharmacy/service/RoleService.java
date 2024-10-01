package com.project.pharmacy.service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.project.pharmacy.dto.request.RoleUpdateRequest;
import com.project.pharmacy.dto.response.RoleResponse;
import com.project.pharmacy.entity.Role;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.RoleMapper;
import com.project.pharmacy.repository.PermissionRepository;
import com.project.pharmacy.repository.RoleRepository;
import com.project.pharmacy.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;

    PermissionRepository permissionRepository;

    UserRepository userRepository;

    RoleMapper roleMapper;

    // Role ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    public RoleResponse updateRole(RoleUpdateRequest request) {
        Role role =
                roleRepository.findById(request.getId()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        var permissions = permissionRepository
                .findAllByNameIn(request.getPermissions())
                .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND));

        roleMapper.updateRole(role, request);
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);

        return roleMapper.toRoleResponse(role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRole(String id) {
        userRepository.removeRoleFromUsers(id);

        roleRepository.deleteById(id);
    }
}
