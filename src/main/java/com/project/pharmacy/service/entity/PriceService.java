package com.project.pharmacy.service.entity;

import java.util.List;

import com.project.pharmacy.dto.response.entity.ProductResponse;
import com.project.pharmacy.entity.Image;
import com.project.pharmacy.mapper.PriceMapper;
import com.project.pharmacy.repository.ImageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.project.pharmacy.dto.request.price.PriceCreateRequest;
import com.project.pharmacy.dto.request.price.PriceUpdateRequest;
import com.project.pharmacy.dto.response.entity.PriceResponse;
import com.project.pharmacy.entity.Price;
import com.project.pharmacy.entity.Product;
import com.project.pharmacy.entity.Unit;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.PriceRepository;
import com.project.pharmacy.repository.ProductRepository;
import com.project.pharmacy.repository.UnitRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PriceService {
    ProductRepository productRepository;

    UnitRepository unitRepository;

    PriceRepository priceRepository;

    ImageRepository imageRepository;

    PriceMapper priceMapper;

    // ADMIN and EMPLOYEE
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

        List<Price> prices = priceRepository.findByProductId(request.getProductId());

        for (Price price1 : prices) {
            if (price1.getPrice() == request.getPrice()) {
                throw new AppException(ErrorCode.PRICE_NOT_BE_EQUAL);
            }
        }

        Price price = Price.builder()
                .product(product)
                .unit(unit)
                .price(request.getPrice())
                .description(request.getDescription())
                .build();
        priceRepository.save(price);

        PriceResponse priceResponse =  priceMapper.toPriceResponse(price);
        priceResponse.setProduct(ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .build());
        return priceResponse;
    }

    public Page<PriceResponse> getPrice(Pageable pageable){
        return priceRepository.findAll(pageable)
                .map(price -> {
                    Image firstImage = imageRepository.findFirstByProductId(price.getProduct().getId());
                    String url = firstImage.getSource();

                    ProductResponse productResponse = ProductResponse.builder()
                            .id(price.getProduct().getId())
                            .name(price.getProduct().getName())
                            .build();

                    PriceResponse priceResponse = priceMapper.toPriceResponse(price);
                    priceResponse.setProduct(productResponse);
                    priceResponse.setImage(url);

                    return priceResponse;
                });
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public void updatePrice(PriceUpdateRequest request) {
        Price price = priceRepository.findById(request.getPriceId())
                .orElseThrow(() -> new AppException(ErrorCode.PRICE_NOT_FOUND));

        int oldPrice = price.getPrice();

        if (request.getPrice() > 0) {
            price.setPrice(request.getPrice());
            price.setDescription(request.getDescription());
            priceRepository.save(price);
        } else {
            throw new AppException(ErrorCode.PRICE_NOT_ZERO);
        }

        List<Price> prices = priceRepository.findByProductId(request.getProductId());

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
    public void deletePrice(String id) {
        Price price = priceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRICE_NOT_FOUND));
        priceRepository.delete(price);
    }
}
