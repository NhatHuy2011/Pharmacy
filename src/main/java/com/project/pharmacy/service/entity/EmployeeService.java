package com.project.pharmacy.service.entity;

import com.project.pharmacy.dto.request.employee.CreateEmployeeRequest;
import com.project.pharmacy.dto.request.employee.UpdateEmployeeRequest;
import com.project.pharmacy.dto.response.entity.EmployeeResponse;
import com.project.pharmacy.dto.response.entity.UserResponse;
import com.project.pharmacy.entity.Employee;
import com.project.pharmacy.entity.Role;
import com.project.pharmacy.entity.User;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.EmployeeMapper;
import com.project.pharmacy.repository.EmployeeRepository;
import com.project.pharmacy.repository.RoleRepository;
import com.project.pharmacy.repository.UserRepository;
import com.project.pharmacy.service.cloudinary.CloudinaryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeService {
    EmployeeRepository employeeRepository;

    PasswordEncoder passwordEncoder;

    RoleRepository roleRepository;

    EmployeeMapper employeeMapper;

    CloudinaryService cloudinaryService;

    //Role ADMIN
    @PreAuthorize("hasRole('ADMIN')")
    public EmployeeResponse createEmployee(CreateEmployeeRequest request, MultipartFile file) throws IOException {
        if (employeeRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.EMPLOYEE_EXISTED);

        if(!request.getPassword().equals(request.getConfirmPassword()))
            throw new AppException(ErrorCode.PASSWORD_RE_ENTERING_INCORRECT);

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        String image;
        if (file != null && !file.isEmpty()) {
            image = cloudinaryService.uploadImage(file);
        } else {
            throw new AppException(ErrorCode.MISS_IMAGE);
        }

        Employee employee = employeeMapper.toEmployee(request);
        employee.setUsername(request.getUsername());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        employee.setFirstname(request.getFirstname());
        employee.setLastname(request.getLastname());
        employee.setImage(image);
        employee.setDob(request.getDob());
        employee.setSex(request.getSex());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setStatus(true);
        employee.setRole(role);
        employeeRepository.save(employee);

        return employeeMapper.toEmployeeResponse(employee);
    }

    @PreAuthorize(("hasRole('ADMIN')"))
    public EmployeeResponse updateEmployee(UpdateEmployeeRequest request, MultipartFile file) throws IOException {
        Employee employee = employeeRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND));

        employeeMapper.updateEmpployee(employee, request);
        employee.setFirstname(request.getFirstname());
        employee.setLastname(request.getLastname());
        employee.setDob(request.getDob());
        employee.setSex(request.getSex());
        employee.setPhoneNumber(request.getPhoneNumber());

        String image;
        if (file != null && !file.isEmpty()) {
            image = cloudinaryService.uploadImage(file);
            employee.setImage(image);
        }

        if(request.getRole()!=null){
            Role role = roleRepository.findByName(request.getRole())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            employee.setRole(role);
        }

        employeeRepository.save(employee);

        return employeeMapper.toEmployeeResponse(employee);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void banEmployee(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND));
        employee.setStatus(false);
        employeeRepository.save(employee);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void unbanEmployee(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.EMPLOYEE_NOT_FOUND));
        employee.setStatus(true);
        employeeRepository.save(employee);
    }


    @PreAuthorize("hasRole('ADMIN')")
    public Page<EmployeeResponse> getAllEmployee(Pageable pageable, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        return employeeRepository.findAllByRole(pageable, role)
                .map(employeeMapper::toEmployeeResponse);
    }
    
    //Role EMPLOYEE, NURSE, DOCTOR
    public EmployeeResponse getInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        Employee employee = employeeRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        EmployeeResponse employeeResponse = employeeMapper.toEmployeeResponse(employee);

        return employeeResponse;
    }
}
