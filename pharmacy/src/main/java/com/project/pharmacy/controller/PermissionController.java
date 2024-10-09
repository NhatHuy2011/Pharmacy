package com.project.pharmacy.controller;

import java.util.List;
import java.util.Objects;

import org.springframework.web.bind.annotation.*;

import com.project.pharmacy.dto.request.PermissionCreateRequest;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.PermissionResponse;
import com.project.pharmacy.service.PermissionService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    //Role ADMIN
    @PostMapping
    public ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionCreateRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.createPermission(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<PermissionResponse>> getAll() {
        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<Objects> deletePermission(@PathVariable String id) {
        permissionService.deletePermission(id);
        return ApiResponse.<Objects>builder()
                .message("Delete Permission Successful")
                .build();
    }
}
