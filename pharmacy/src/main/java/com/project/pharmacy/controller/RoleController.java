package com.project.pharmacy.controller;

import com.project.pharmacy.dto.request.RoleCreateRequest;
import com.project.pharmacy.dto.request.RoleUpdateRequest;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.RoleResponse;
import com.project.pharmacy.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/role")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAll(){
        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getAll())
                .build();
    }

    @PutMapping
    public ApiResponse<RoleResponse> updateRole(@RequestBody RoleUpdateRequest request){
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.updateRole(request))
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<Objects> deleteRole(@PathVariable String id){
        roleService.deleteRole(id);
        return ApiResponse.<Objects>builder()
                .message("Delete Role Successful")
                .build();
    }
}