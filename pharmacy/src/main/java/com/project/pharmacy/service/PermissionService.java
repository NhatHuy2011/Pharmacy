package com.project.pharmacy.service;

import com.project.pharmacy.dto.request.PermissionCreateRequest;
import com.project.pharmacy.dto.response.PermissionResponse;
import com.project.pharmacy.entity.Permission;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.PermissionMapper;
import com.project.pharmacy.repository.PermissionRepository;
import com.project.pharmacy.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;

    RoleRepository roleRepository;

    PermissionMapper permissionMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public PermissionResponse createPermission(PermissionCreateRequest request){
        if(permissionRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.PERMISSION_EXISTED);

        Permission permission = permissionMapper.toPermission(request);
        permissionRepository.save(permission);

        return permissionMapper.toPermissionResponse(permission);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<PermissionResponse> getAll(){
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toPermissionResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deletePermission(String id){
        roleRepository.removePermissionFromRoles(id);

        permissionRepository.deleteById(id);
    }
}
