package com.project.pharmacy.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import com.project.pharmacy.dto.response.PriceResponse;
import com.project.pharmacy.dto.response.UnitResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.pharmacy.dto.request.ProductCreateRequest;
import com.project.pharmacy.dto.request.ProductUpdateRequest;
import com.project.pharmacy.dto.response.ProductResponse;
import com.project.pharmacy.entity.*;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.mapper.ProductMapper;
import com.project.pharmacy.repository.*;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;

    CategoryRepository categoryRepository;

    CompanyRepository companyRepository;

    CloudinaryService cloudinaryService;

    ImageRepository imageRepository;

    PriceRepository priceRepository;

    ProductMapper productMapper;

    // ADMIN and EMPLOYEE
    // Thêm sản phẩm
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ProductResponse createProduct(ProductCreateRequest request, List<MultipartFile> multipartFiles)
            throws IOException {
        Category category = categoryRepository
                .findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Company company = companyRepository
                .findById(request.getCompanyId())
                .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));

        if (productRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }

        // Mapper
        Product product = productMapper.toProduct(request);
        product.setCategory(category);
        product.setCompany(company);
        product.setDateCreation(LocalDate.now());
        productRepository.save(product);

        List<String> imageUrls = new ArrayList<>();
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            for (MultipartFile multipartFile : multipartFiles) {
                String urlImage = cloudinaryService.uploadImage(multipartFile);
                imageUrls.add(urlImage);
                Image image = Image.builder()
                        .product(product)
                        .source(urlImage)
                        .build();
                imageRepository.save(image);
            }
        } else {
            throw new AppException(ErrorCode.EMPTY_FILE);
        }

        ProductResponse productResponse = productMapper.toProductResponse(product);
        productResponse.setImages(imageUrls);
        return productResponse;
    }

    // Cập nhật sản phẩm
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ProductResponse updateProduct(ProductUpdateRequest request, List<MultipartFile> files) throws IOException {
        Product product = productRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Cập nhật thông tin
        productMapper.updateProduct(product, request);

        if (request.getDateExpiration().isAfter(product.getDateCreation()))
            product.setDateExpiration(request.getDateExpiration());
        else
            throw new AppException(ErrorCode.PRODUCT_EXPIRATION_INVALID);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        }

        if (request.getCompanyId() != null) {
            Company company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new AppException(ErrorCode.COMPANY_NOT_FOUND));
            product.setCompany(company);
        }

        List<String> imageUrls;
        if (files != null && !files.isEmpty()) {
            imageRepository.deleteAllByProductId(product.getId());
            imageUrls = files.stream()
                    .map(multipartFile -> {
                        try {
                            String urlImage = cloudinaryService.uploadImage(multipartFile);
                            imageRepository.save(Image.builder()
                                    .product(product)
                                    .source(urlImage)
                                    .build());
                            return urlImage;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        } else {
            imageUrls = imageRepository.findByProductId(product.getId()).stream()
                    .map(Image::getSource)
                    .collect(Collectors.toList());
        }

        ProductResponse productResponse = productMapper.toProductResponse(product);
        productResponse.setImages(imageUrls);
        return productResponse;
    }

    // Xoá sản phẩm
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        imageRepository.deleteAllByProductId(product.getId());
        priceRepository.deleteAllByProductId(id);
        productRepository.deleteById(product.getId());
    }

    // Role USER
    // Xem danh sách sản phẩm
    public Page<ProductResponse> getAllProduct(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(product -> {
            // Lấy hình ảnh đầu tiên
            Image firstImage = imageRepository.findFirstByProductId(product.getId());
            String url = firstImage != null ? firstImage.getSource() : null;

            // Lấy danh sách đơn vị và giá sản phẩm
            Set<Price> prices = priceRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRICE_NOT_FOUND));

            Set<PriceResponse> priceResponses = prices.stream()
                    .map(price -> {
                        return PriceResponse.builder()
                                .id(price.getId())
                                .unit(UnitResponse.builder()
                                        .id(price.getUnit().getId())
                                        .name(price.getUnit().getName())
                                        .build())
                                .price(price.getPrice())
                                .description(price.getDescription())
                                .build();
                    })
                    .collect(Collectors.toSet());

            ProductResponse productResponse = productMapper.toProductResponse(product);
            productResponse.setPrices(priceResponses);
            productResponse.setImage(url);

            return productResponse;
        });
    }

    // Xem theo danh muc
    public Page<ProductResponse> getProductByCategoryAsc(Pageable pageable, String categoryId) {

        List<String> categoryIds = getAllCategoryIds(categoryId);

        return productRepository.findByCategoryIdsAsc(pageable, categoryIds)
                .map(product -> {
            // Lấy hình ảnh đầu tiên
            Image firstImage = imageRepository.findFirstByProductId(product.getId());
            String url = firstImage != null ? firstImage.getSource() : null;

            // Lấy danh sách đơn vị và giá sản phẩm
            Set<Price> prices = priceRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRICE_NOT_FOUND));

            Set<PriceResponse> priceResponses = prices.stream()
                    .map(price -> {
                        return PriceResponse.builder()
                                .id(price.getId())
                                .unit(UnitResponse.builder()
                                        .id(price.getUnit().getId())
                                        .name(price.getUnit().getName())
                                        .build())
                                .price(price.getPrice())
                                .description(price.getDescription())
                                .build();
                    })
                    .collect(Collectors.toSet());

            ProductResponse productResponse = productMapper.toProductResponse(product);
            productResponse.setPrices(priceResponses);
            productResponse.setImage(url);

            return productResponse;
        });
    }

    public Page<ProductResponse> getProductByCategoryDesc(Pageable pageable, String categoryId) {

        List<String> categoryIds = getAllCategoryIds(categoryId);

        return productRepository.findByCategoryIdsDesc(pageable, categoryIds)
                .map(product -> {
                    // Lấy hình ảnh đầu tiên
                    Image firstImage = imageRepository.findFirstByProductId(product.getId());
                    String url = firstImage != null ? firstImage.getSource() : null;

                    // Lấy danh sách đơn vị và giá sản phẩm
                    Set<Price> prices = priceRepository.findByProductId(product.getId())
                            .orElseThrow(() -> new AppException(ErrorCode.PRICE_NOT_FOUND));

                    Set<PriceResponse> priceResponses = prices.stream()
                            .map(price -> {
                                return PriceResponse.builder()
                                        .id(price.getId())
                                        .unit(UnitResponse.builder()
                                                .id(price.getUnit().getId())
                                                .name(price.getUnit().getName())
                                                .build())
                                        .price(price.getPrice())
                                        .description(price.getDescription())
                                        .build();
                            })
                            .collect(Collectors.toSet());

                    ProductResponse productResponse = productMapper.toProductResponse(product);
                    productResponse.setPrices(priceResponses);
                    productResponse.setImage(url);

                    return productResponse;
                });
    }

    private List<String> getAllCategoryIds(String parentId) {
        List<String> categoryIds = new ArrayList<>();
        getCategoryIdsRecursively(parentId, categoryIds);
        return categoryIds;
    }

    private void getCategoryIdsRecursively(String parentId, List<String> categoryIds) {
        categoryIds.add(parentId);
        List<Category> subCategories = categoryRepository.findAllByParentId(parentId);
        for (Category subCategory : subCategories) {
            getCategoryIdsRecursively(subCategory.getId(), categoryIds);
        }
    }

    // Xem chi tiết sản phẩm
    public List<ProductResponse> getOne(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        List<String> imageUrls = imageRepository.findByProductId(product.getId()).stream()
                .map(Image::getSource)
                .collect(Collectors.toList());

        return priceRepository.findByProductId(product.getId())
                .map(prices -> prices.stream()
                        .map(price -> {
                            ProductResponse productResponse = productMapper.toProductResponse(product);
                            productResponse.setPrice(PriceResponse.builder()
                                            .id(price.getId())
                                            .unit(UnitResponse.builder()
                                                    .id(price.getUnit().getId())
                                                    .name(price.getUnit().getName())
                                                    .build())
                                            .price(price.getPrice())
                                            .description(price.getDescription())
                                            .build());
                            productResponse.setImages(imageUrls);
                            return productResponse;
                })
                .collect(Collectors.toList())
            )
            .orElseThrow(() -> new AppException(ErrorCode.PRICE_NOT_FOUND));
    }
}
