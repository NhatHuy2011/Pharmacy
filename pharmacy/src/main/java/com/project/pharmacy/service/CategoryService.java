package com.project.pharmacy.service;

import com.project.pharmacy.dto.request.CategoryCreateRequest;
import com.project.pharmacy.dto.request.CategoryUpdateRequest;
import com.project.pharmacy.dto.response.CategoryResponse;
import com.project.pharmacy.entity.Category;
import com.project.pharmacy.entity.Product;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.CategoryMapper;
import com.project.pharmacy.repository.CategoryRepository;
import com.project.pharmacy.repository.ImageRepository;
import com.project.pharmacy.repository.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;

    ImageService imageService;

    ProductRepository productRepository;

    ImageRepository imageRepository;

    CategoryMapper categoryMapper;
    //Role ADMIN
    //Them danh muc
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(CategoryCreateRequest request, MultipartFile multipartFile) throws IOException {
        if(categoryRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.CATEGORY_EXISTED);

        //Check parent
        Category parent = null;
        if(request.getParent() != null){
            parent = categoryRepository.findById(request.getParent())
                    .orElseThrow(() -> new AppException(ErrorCode.PARENT_CATEGORY_NOT_FOUND));
        }

        //Image
        String urlImage = imageService.uploadImage(multipartFile);

        //Mapper
        Category category = categoryMapper.toCategory(request);
        category.setParent(parent);
        category.setImage(urlImage);
        categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    //Sua danh muc
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(CategoryUpdateRequest request, MultipartFile multipartFile) throws IOException{
        Category category = categoryRepository.findById(request.getId())
                .orElseThrow(()->new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        //Check parent
        Category parent = null;
        if (request.getParent() != null) {
            parent = categoryRepository.findById(request.getParent())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        }

        if(multipartFile!=null && !multipartFile.isEmpty()){
            String urlImage = imageService.uploadImage(multipartFile);

            //Mapper
            categoryMapper.updateCategory(category, request);
            category.setParent(parent);
            category.setImage(urlImage);
        }
        else{
            categoryMapper.updateCategory(category, request);
            category.setParent(parent);
        }

        categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(category);
    }

    //Xoa danh muc
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(String id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        List<Product> products = productRepository.findByCategoryId(id);
        for(Product product: products){
            imageRepository.deleteAllByProductId(product.getId());
        }
        productRepository.deleteAllByCategoryId(id);
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

    //Role USER
    //Xem danh muc goc
    public List<CategoryResponse> getRootCategories(){
        return categoryRepository.findByParent(null).stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }

    //Xem danh muc con cua mot danh muc
    public List<CategoryResponse> getSubCategories(String parentId){
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow();
        return categoryRepository.findByParent(parent).stream()
                .map(categoryMapper::toCategoryResponse)
                .collect(Collectors.toList());
    }
}
