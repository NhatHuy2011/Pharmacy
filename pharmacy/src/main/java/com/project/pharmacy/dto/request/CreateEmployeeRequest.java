package com.project.pharmacy.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateEmployeeRequest {
    @Size(min = 3, message = "Tên đăng nhập phải có ít nhất 3 kí tự")
    String username;
}
