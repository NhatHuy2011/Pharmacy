package com.project.pharmacy.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.project.pharmacy.dto.request.category.CategoryCreateRequest;
import com.project.pharmacy.dto.request.category.CategoryUpdateRequest;
import com.project.pharmacy.dto.response.entity.CategoryResponse;
import com.project.pharmacy.entity.Category;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {
    @Mapping(target = "parent", ignore = true)
    Category toCategory(CategoryCreateRequest request);

    CategoryResponse toCategoryResponse(Category category);

    @Mapping(target = "parent", ignore = true)
    void updateCategory(@MappingTarget Category category, CategoryUpdateRequest request);
}
