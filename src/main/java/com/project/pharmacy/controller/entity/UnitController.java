package com.project.pharmacy.controller.entity;

import java.util.Objects;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import com.project.pharmacy.dto.request.unit.UnitCreateRequest;
import com.project.pharmacy.dto.request.unit.UnitUpdateRequest;
import com.project.pharmacy.dto.response.common.ApiResponse;
import com.project.pharmacy.dto.response.entity.UnitResponse;
import com.project.pharmacy.service.entity.UnitService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/unit")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UnitController {
    UnitService unitService;

    // Role ADMIN and EMPLOYEE
    @GetMapping
    public ApiResponse<Page<UnitResponse>> getUnit(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UnitResponse> unitResponses = unitService.getUnit(pageable);

        return ApiResponse.<Page<UnitResponse>>builder().result(unitResponses).build();
    }

    @PostMapping
    public ApiResponse<UnitResponse> createUnit(@Valid @RequestBody UnitCreateRequest request) {
        return ApiResponse.<UnitResponse>builder()
                .result(unitService.createUnit(request))
                .build();
    }

    @PutMapping
    public ApiResponse<UnitResponse> updateUnit(@RequestBody UnitUpdateRequest request) {
        return ApiResponse.<UnitResponse>builder()
                .result(unitService.updateUnit(request))
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<Objects> deleteUnit(@PathVariable("id") String id) {
        unitService.deleteUnit(id);
        return ApiResponse.<Objects>builder().message("Delete Unit Successful").build();
    }
}
