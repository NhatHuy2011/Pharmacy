package com.project.pharmacy.mapper;

import com.project.pharmacy.dto.request.CategoryCreateRequest;
import com.project.pharmacy.dto.request.CategoryUpdateRequest;
import com.project.pharmacy.dto.response.CategoryResponse;
import com.project.pharmacy.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "parent", ignore = true)
    Category toCategory(CategoryCreateRequest request);

    @Mapping(source = "parent.name", target = "parent")
    CategoryResponse toCategoryResponse(Category category);

    @Mapping(target = "parent", ignore = true)
    void updateCategory(@MappingTarget Category category, CategoryUpdateRequest request);
}
