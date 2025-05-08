package com.project.pharmacy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.project.pharmacy.dto.request.product.ProductCreateRequest;
import com.project.pharmacy.dto.request.product.ProductUpdateRequest;
import com.project.pharmacy.dto.response.entity.ProductResponse;
import com.project.pharmacy.entity.Product;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    Product toProduct(ProductCreateRequest request);

    @Mapping(target = "images", ignore = true)
    @Mapping(target = "prices", ignore = true)
    ProductResponse toProductResponse(Product product);
    
    void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);
}
