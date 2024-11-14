package com.project.pharmacy.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
                Image image = Image.builder().product(product).source(urlImage).build();
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
        Product product = productRepository
                .findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        // Cập nhật thông tin
        productMapper.updateProduct(product, request);

        if (request.getDateExpiration().isAfter(product.getDateCreation()))
            product.setDateExpiration(request.getDateExpiration());
        else
            throw new AppException(ErrorCode.PRODUCT_EXPIRATION_INVALID);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository
                    .findById(request.getCategoryId())
                    .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            product.setCategory(category);
        }

        if (request.getCompanyId() != null) {
            Company company = companyRepository
                    .findById(request.getCompanyId())
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
        return productRepository.findAll(pageable).map(product -> {
            // Lấy hình ảnh đầu tiên
            Image firstImage = imageRepository.findFirstByProductId(product.getId());
            String url = firstImage != null ? firstImage.getSource() : null;

            // Lấy danh sách đơn vị và giá sản phẩm
            Set<Price> prices = priceRepository.findByProductId(product.getId());
            Set<Integer> price = prices.stream()
                    .map(Price::getPrice)
                    .sorted(Comparator.reverseOrder()) // Sap xep gia giam dan
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            Set<String> unit_name = prices.stream()
                    .map(productUnit -> productUnit.getUnit().getName())
                    .collect(Collectors.toSet());
            Set<String> unit_id = prices.stream()
                    .map(productUnit -> productUnit.getUnit().getId())
                    .collect(Collectors.toSet());
            ProductResponse productResponse = productMapper.toProductResponse(product);
            productResponse.setUnit_all_id(unit_id);
            productResponse.setUnit_all(unit_name);
            productResponse.setPrice_all(price);
            productResponse.setImage(url);

            return productResponse;
        });
    }

    // Xem theo danh muc
    public Page<ProductResponse> getProductByCategory(Pageable pageable, String categoryId) {
        return productRepository.findByCategoryId(pageable, categoryId)
                .map(product -> {
            // Lấy hình ảnh đầu tiên
            Image firstImage = imageRepository.findFirstByProductId(product.getId());
            String url = firstImage != null ? firstImage.getSource() : null;

            // Lấy danh sách đơn vị và giá sản phẩm
            Set<Price> prices = priceRepository.findByProductId(product.getId());
            Set<Integer> price = prices.stream()
                    .map(Price::getPrice)
                    .sorted(Comparator.reverseOrder()) // Sap xep gia giam dan
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            Set<String> unit_name = prices.stream()
                    .map(productUnit -> productUnit.getUnit().getName())
                    .collect(Collectors.toSet());
            Set<String> unit_id = prices.stream()
                    .map(productUnit -> productUnit.getUnit().getId())
                    .collect(Collectors.toSet());
            ProductResponse productResponse = productMapper.toProductResponse(product);
            productResponse.setUnit_all_id(unit_id);
            productResponse.setUnit_all(unit_name);
            productResponse.setPrice_all(price);
            productResponse.setImage(url);

            return productResponse;
        });
    }

    // Xem chi tiết sản phẩm
    public List<ProductResponse> getOne(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        List<String> imageUrls = imageRepository.findByProductId(product.getId()).stream()
                .map(Image::getSource)
                .collect(Collectors.toList());

        return priceRepository.findByProductId(product.getId())
                .stream()
                .map(price -> {
                    ProductResponse productResponse = productMapper.toProductResponse(product);
                    productResponse.setUnit_one_id(price.getUnit().getId());
                    productResponse.setUnit_one(price.getUnit().getName());
                    productResponse.setPrice_one(price.getPrice());
                    productResponse.setImages(imageUrls);
                    return productResponse;
                })
                .collect(Collectors.toList());
    }
}
