package com.project.pharmacy.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.project.pharmacy.dto.response.entity.RoleResponse;
import com.project.pharmacy.mapper.RoleMapper;
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

    UserRepository userRepository;

    RoleMapper roleMapper;

    // Role ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRole(String id) {
        userRepository.removeRoleFromUsers(id);

        roleRepository.deleteById(id);
    }
}
