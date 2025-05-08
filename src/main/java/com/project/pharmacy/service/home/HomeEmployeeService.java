package com.project.pharmacy.service.home;

import com.project.pharmacy.repository.OrderRepository;
import com.project.pharmacy.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomeEmployeeService {
    ProductRepository productRepository;

    OrderRepository orderRepository;

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public int getTotalProduct(){
        return productRepository.getTotalProduct();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public Long getTotalOrderNotConfirm(){
        return orderRepository.totalOrderNotConfirm();
    }
}
