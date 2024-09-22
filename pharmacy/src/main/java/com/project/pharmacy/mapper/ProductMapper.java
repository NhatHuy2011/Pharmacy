package com.project.pharmacy.mapper;

import com.project.pharmacy.dto.request.ProductCreateRequest;
import com.project.pharmacy.dto.request.ProductUpdateRequest;
import com.project.pharmacy.dto.response.ProductResponse;
import com.project.pharmacy.entity.Image;
import com.project.pharmacy.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "company", ignore = true)
    Product toProduct(ProductCreateRequest request);

    @Mapping(target = "images", ignore = true)
    @Mapping(target = "price_all", ignore = true)
    @Mapping(target = "price_one", ignore = true)
    @Mapping(target = "unit_all", ignore = true)
    @Mapping(target = "unit_one", ignore = true)
    @Mapping(target = "image", ignore = true)
    ProductResponse toProductResponse(Product product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "company", ignore = true)
    void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);
}
