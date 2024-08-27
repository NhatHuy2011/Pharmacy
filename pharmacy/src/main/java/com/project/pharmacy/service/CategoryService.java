package com.project.pharmacy.service;

import com.project.pharmacy.dto.request.CategoryCreateRequest;
import com.project.pharmacy.dto.request.CategoryUpdateRequest;
import com.project.pharmacy.dto.response.CategoryResponse;
import com.project.pharmacy.entity.Category;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.CategoryRepository;
import com.project.pharmacy.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;

    ImageService imageService;

    ProductRepository productRepository;
    //Xem danh muc goc
    public List<CategoryResponse> getRootCategories(){
        List<Category> categories = categoryRepository.findByParent(null);
        List<CategoryResponse> categoryResponses = new ArrayList<>();

        for(Category category : categories){
            CategoryResponse temp = CategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .build();
            categoryResponses.add(temp);
        }
        return categoryResponses;
    }

    //Xem danh muc con cua mot danh muc
    public List<CategoryResponse> getSubCategories(String parentId){
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow();
        List<Category> categories = categoryRepository.findByParent(parent);
        List<CategoryResponse> categoryResponses = new ArrayList<>();

        for(Category category : categories){
            CategoryResponse temp = CategoryResponse.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .parent(category.getParent().getName())
                    .build();
            categoryResponses.add(temp);
        }
        return categoryResponses;
    }

    //Them danh muc
    public CategoryResponse createCategory(CategoryCreateRequest request, MultipartFile multipartFile) throws IOException {
        if(categoryRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.CATEGORY_EXISTED);

        Category parent = null;
        if(request.getParent() != null){
            parent = categoryRepository.findById(request.getParent())
                    .orElseThrow(() -> new AppException(ErrorCode.PARENT_CATEGORY_NOT_FOUND));
        }

        String urlImage = imageService.uploadImage(multipartFile);

        Category category = new Category();
        category.setName(request.getName());
        category.setImage(urlImage);
        category.setDescription(request.getDescription());
        category.setParent(parent);
        categoryRepository.save(category);

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .image(urlImage)
                .description(category.getDescription())
                .parent(category.getParent() != null ? category.getParent().getName() : null)
                .build();
    }

    //Sua danh muc
    public CategoryResponse updateCategory(String id, CategoryUpdateRequest request, MultipartFile multipartFile) throws IOException{
        Category category = categoryRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Category parent = null;
        if (request.getParent() != null) {
            parent = categoryRepository.findById(request.getParent())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        if(multipartFile!=null && !multipartFile.isEmpty()){
            String urlImage = imageService.uploadImage(multipartFile);

            category.setName(request.getName());
            category.setDescription(request.getDescription());
            category.setImage(urlImage);
            category.setParent(parent);
        }
        else{
            category.setName(request.getName());
            category.setDescription(request.getDescription());
            category.setParent(parent);
        }

        categoryRepository.save(category);

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .image(category.getImage())
                .description(category.getDescription())
                .parent(parent!= null ? parent.getName() : null)
                .build();
    }

    //Xoa danh muc
    @Transactional
    public void deleteCategory(String id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        productRepository.updateCategoryIdToNull(id);
        deleteCategoryRecursive(category);
    }

    @Transactional
    protected void deleteCategoryRecursive(Category category){
        List<Category> subCategories = categoryRepository.findByParent(category);
        for (Category subCategory : subCategories) {
            deleteCategoryRecursive(subCategory);
        }
        categoryRepository.delete(category);
    }
}
