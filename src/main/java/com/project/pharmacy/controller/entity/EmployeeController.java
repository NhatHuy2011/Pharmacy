package com.project.pharmacy.controller.entity;

import com.project.pharmacy.dto.request.employee.CreateEmployeeRequest;
import com.project.pharmacy.dto.request.employee.UpdateEmployeeRequest;
import com.project.pharmacy.dto.response.common.ApiResponse;
import com.project.pharmacy.dto.response.entity.EmployeeResponse;
import com.project.pharmacy.dto.response.entity.UserResponse;
import com.project.pharmacy.service.entity.EmployeeService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeController {
    EmployeeService employeeService;

    //Role ADMIN
    @PostMapping("/employee")
    public ApiResponse<EmployeeResponse> createEmployee(@RequestPart("createEmployee") CreateEmployeeRequest request,
                                                        @RequestPart("file")MultipartFile file) throws IOException {
        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.createEmployee(request, file))
                .build();
    }

    @PutMapping("/employee")
    public ApiResponse<EmployeeResponse> updateEmployee(@RequestPart("updateEmployee") UpdateEmployeeRequest request,
                                                        @RequestPart("file") MultipartFile file) throws IOException {
        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.updateEmployee(request, file))
                .build();
    }

    @GetMapping("/employee")
    public ApiResponse<Page<EmployeeResponse>> getAllEmployee(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam("roleName") String name) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeResponse> employeeResponses = employeeService.getAllEmployee(pageable, name);
        return ApiResponse.<Page<EmployeeResponse>>builder()
                .result(employeeResponses)
                .build();
    }

    @PutMapping("/employee/ban/{id}")
    public ApiResponse<Void> banEmployee(@PathVariable String id){
        employeeService.banEmployee(id);
        return ApiResponse.<Void>builder()
                .message("Ban employee successful")
                .build();
    }

    @PutMapping("/employee/unban/{id}")
    public ApiResponse<Void> unbanEmployee(@PathVariable String id){
        employeeService.unbanEmployee(id);
        return ApiResponse.<Void>builder()
                .message("Unban employee successful")
                .build();
    }

    //Role EMPLOYEE, NURSE, DOCTOR
    @GetMapping("/employee/info")
    public ApiResponse<EmployeeResponse> getEmployeeInfo(){
        return ApiResponse.<EmployeeResponse>builder()
                .result(employeeService.getInfo())
                .build();
    }
}
