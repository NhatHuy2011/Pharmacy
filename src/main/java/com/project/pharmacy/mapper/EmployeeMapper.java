package com.project.pharmacy.mapper;

import com.project.pharmacy.dto.request.employee.CreateEmployeeRequest;
import com.project.pharmacy.dto.response.entity.EmployeeResponse;
import com.project.pharmacy.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeMapper {
    Employee toEmployee(CreateEmployeeRequest request);

    EmployeeResponse toEmployeeResponse(Employee employee);
}
