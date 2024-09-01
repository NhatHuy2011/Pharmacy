package com.project.pharmacy.service;

import com.project.pharmacy.dto.request.ProductUnitCreateRequest;
import com.project.pharmacy.dto.request.ProductUnitUpdateRequest;
import com.project.pharmacy.dto.request.ProductUpdateRequest;
import com.project.pharmacy.dto.response.ProductUnitResponse;
import com.project.pharmacy.entity.Product;
import com.project.pharmacy.entity.ProductUnit;
import com.project.pharmacy.entity.Unit;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.ProductRepository;
import com.project.pharmacy.repository.ProductUnitRepository;
import com.project.pharmacy.repository.UnitRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductUnitService {
    ProductRepository productRepository;

    UnitRepository unitRepository;

    ProductUnitRepository productUnitRepository;

    //Thêm giá và đơn vị cho sản phẩm
    public ProductUnitResponse creatProductUnit(ProductUnitCreateRequest request){
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(()->new AppException(ErrorCode.UNIT_NOT_FOUND));
        if(productUnitRepository.existsByProductAndUnit(product, unit)){
            throw new AppException(ErrorCode.PRODUCT_UNIT_EXISTED);
        }
        ProductUnit productUnit = new ProductUnit();
        if(request.getPrice() > 0) {
            productUnit.setProduct(product);
            productUnit.setUnit(unit);
            productUnit.setPrice(request.getPrice());
            productUnit.setDescription(request.getDescription());
            productUnitRepository.save(productUnit);
        } else {
            throw new AppException(ErrorCode.PRICE_NOT_ZERO);
        }
        return ProductUnitResponse.builder()
                .id(productUnit.getId())
                .productName(productUnit.getProduct().getName())
                .unitName(productUnit.getUnit().getName())
                .price(productUnit.getPrice())
                .description(productUnit.getDescription())
                .build();
    }

    //Cập nhật giá và đơn vị cho sản phẩm
    public void updateProductUnit(ProductUnitUpdateRequest request){
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(()->new AppException(ErrorCode.UNIT_NOT_FOUND));
        ProductUnit productUnit = productUnitRepository.findByProductAndUnit(product, unit);
        List<ProductUnit> productUnits = productUnitRepository.findByProductId(request.getProductId());

        //Tính toán tỉ lệ
        double priceRatio = (double) request.getPrice()/ productUnit.getPrice();

        if(request.getPrice() > 0) {
            productUnit.setPrice(request.getPrice());
            productUnit.setDescription(request.getDescription());
            productUnitRepository.save(productUnit);
        }else {
            throw new AppException(ErrorCode.PRICE_NOT_ZERO);
        }

        for(ProductUnit productUnit1:productUnits){
            if(!productUnit1.getUnit().getId().equals(productUnit.getUnit().getId())){
                //Cap nhat gia dua tren ti le thay doi
                int newPrice = (int) (productUnit1.getPrice() * priceRatio);
                productUnit1.setPrice(newPrice);
                productUnitRepository.save(productUnit1);
            }
        }
    }

    @Transactional
    public void deleteProductUnit(String id){
        productUnitRepository.deleteAllByUnitId(id);
    }
}
