package com.project.pharmacy.controller;

import com.project.pharmacy.dto.request.UnitCreateRequest;
import com.project.pharmacy.dto.request.UnitUpdateRequest;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.UnitResponse;
import com.project.pharmacy.service.UnitService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/unit")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UnitController {
    UnitService unitService;

    @GetMapping
    public ApiResponse<List<UnitResponse>> getUnit(){
        return ApiResponse.<List<UnitResponse>>builder()
                .result(unitService.getUnit())
                .build();
    }

    @PostMapping
    public ApiResponse<UnitResponse> createUnit(@Valid @RequestBody UnitCreateRequest request){
        return ApiResponse.<UnitResponse>builder()
                .result(unitService.createUnit(request))
                .build();
    }

    @PutMapping
    public ApiResponse<UnitResponse> updateUnit(@RequestBody UnitUpdateRequest request){
        return ApiResponse.<UnitResponse>builder()
                .result(unitService.updateUnit(request))
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<Objects> deleteUnit(@PathVariable("id") String id){
        unitService.deleteUnit(id);
        return ApiResponse.<Objects>builder()
                .message("Delete Unit Successful")
                .build();
    }
}
