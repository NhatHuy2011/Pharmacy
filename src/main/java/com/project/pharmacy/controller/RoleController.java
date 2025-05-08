package com.project.pharmacy.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.web.bind.annotation.*;

import com.project.pharmacy.dto.response.common.ApiResponse;
import com.project.pharmacy.dto.response.entity.RoleResponse;
import com.project.pharmacy.service.RoleService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequiredArgsConstructor
@RequestMapping("/role")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    // Role ADMIN
    @GetMapping
    public ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<Objects> deleteRole(@PathVariable String id) {
        roleService.deleteRole(id);
        return ApiResponse.<Objects>builder().message("Delete Role Successful").build();
    }
}
