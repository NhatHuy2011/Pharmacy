package com.project.pharmacy.service;

import com.project.pharmacy.dto.request.PriceCreateRequest;
import com.project.pharmacy.dto.request.PriceUpdateRequest;
import com.project.pharmacy.dto.response.PriceResponse;
import com.project.pharmacy.entity.Product;
import com.project.pharmacy.entity.Price;
import com.project.pharmacy.entity.Unit;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.ProductRepository;
import com.project.pharmacy.repository.PriceRepository;
import com.project.pharmacy.repository.UnitRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PriceService {
    ProductRepository productRepository;

    UnitRepository unitRepository;

    PriceRepository priceRepository;

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public PriceResponse createPrice(PriceCreateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new AppException(ErrorCode.UNIT_NOT_FOUND));

        if (priceRepository.existsByProductAndUnit(product, unit)) {
            throw new AppException(ErrorCode.PRICE_EXISTED);
        }

        if (request.getPrice() <= 0) {
            throw new AppException(ErrorCode.PRICE_NOT_ZERO);
        }

        Set<Price> prices = priceRepository.findByProductId(request.getProductId());
        for (Price price1 : prices) {
            if (price1.getPrice() == request.getPrice()) {
                throw new AppException(ErrorCode.PRICE_NOT_BE_EQUAL);
            }
        }

        Price price = new Price();
        price.setProduct(product);
        price.setUnit(unit);
        price.setPrice(request.getPrice());
        price.setDescription(request.getDescription());
        priceRepository.save(price);

        return PriceResponse.builder()
                .id(price.getId())
                .product(price.getProduct().getName())
                .unit(price.getUnit().getName())
                .price(price.getPrice())
                .description(price.getDescription())
                .build();
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public void updatePrice(PriceUpdateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new AppException(ErrorCode.UNIT_NOT_FOUND));

        Price price = priceRepository.findByProductAndUnit(product, unit);
        if (price == null) {
            throw new AppException(ErrorCode.PRICE_NOT_FOUND);  // Nếu giá không tồn tại
        }

        int oldPrice = price.getPrice();

        if (request.getPrice() > 0) {
            price.setPrice(request.getPrice());
            price.setDescription(request.getDescription());
            priceRepository.save(price);
        } else {
            throw new AppException(ErrorCode.PRICE_NOT_ZERO);
        }

        Set<Price> prices = priceRepository.findByProductId(request.getProductId());

        double priceRatio = (double) request.getPrice() / oldPrice;

        for (Price price1 : prices) {
            if (!price1.getUnit().getId().equals(price.getUnit().getId())) {
                int newPrice = (int) (price1.getPrice() * priceRatio);
                price1.setPrice(newPrice);
                priceRepository.save(price1);
            }
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public void deletePrice(String id){
        priceRepository.deleteAllByUnitId(id);
    }
}
