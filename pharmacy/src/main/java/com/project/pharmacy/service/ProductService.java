package com.project.pharmacy.service;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;

    CategoryRepository categoryRepository;

    CompanyRepository companyRepository;

    ImageService imageService;

    ImageRepository imageRepository;

    ProductUnitRepository productUnitRepository;

    ProductMapper productMapper;
    //Thêm sản phẩm
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse createProduct(ProductCreateRequest request, List<MultipartFile> multipartFiles) throws IOException {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(()-> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(()->new AppException(ErrorCode.COMPANY_NOT_FOUND));

        if(productRepository.existsByName(request.getName())){
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }

        //Mapper
        Product product = productMapper.toProduct(request);
        product.setCategory(category);
        product.setCompany(company);
        productRepository.save(product);

        List<String> imageUrls = new ArrayList<>();
        if(multipartFiles != null && !multipartFiles.isEmpty()){
            for (MultipartFile multipartFile : multipartFiles) {
                String urlImage = imageService.uploadImage(multipartFile);
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

    //Xem danh sách sản phẩm
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductResponse> getAllProduct() {
        return productRepository.findAll().stream()
                .map(product -> {
                    //Lấy hình ảnh đầu tiên
                    Image firstImage = imageRepository.findFirstByProductId(product.getId());
                    String url = firstImage != null ? firstImage.getSource() : null;

                    //Lấy danh sách đơn vị và giá sản phẩm
                    List<ProductUnit> productUnits = productUnitRepository.findByProductId(product.getId());
                    List<Integer> price = productUnits.stream()
                            .map(ProductUnit::getPrice)
                            .collect(Collectors.toList());
                    List<String> unit = productUnits.stream()
                            .map(productUnit -> productUnit.getUnit().getName())
                            .collect(Collectors.toList());

                    ProductResponse productResponse = productMapper.toProductResponse(product);
                    productResponse.setPrice1(price);
                    productResponse.setUnit1(unit);
                    productResponse.setImage(url);

                    return productResponse;
                })
                .collect(Collectors.toList());
    }


    //Cập nhật sản phẩm
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProduct(ProductUpdateRequest request, List<MultipartFile> files) throws IOException{
        Product product = productRepository.findById(request.getId())
                .orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        //Cập nhật thông tin
        productMapper.updateProduct(product, request);

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
                            String urlImage = imageService.uploadImage(multipartFile);
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
            // Truy vấn danh sách hình ảnh từ ImageRepository
            imageUrls = imageRepository.findByProductId(product.getId())
                    .stream()
                    .map(Image::getSource)
                    .collect(Collectors.toList());
        }

        ProductResponse productResponse = productMapper.toProductResponse(product);
        productResponse.setImages(imageUrls);
        return productResponse;
    }

    //Lấy 1 sản phẩm
    public List<ProductResponse> getOne(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        List<String> imageUrls = imageRepository.findByProductId(product.getId())
                .stream()
                .map(Image::getSource)
                .collect(Collectors.toList());

        return productUnitRepository.findByProductId(product.getId())
                .stream()
                .map(productUnit -> {
                    ProductResponse productResponse = productMapper.toProductResponse(product);
                    productResponse.setPrice(productUnit.getPrice());
                    productResponse.setUnit(productUnit.getUnit().getName());
                    productResponse.setImages(imageUrls);
                    return productResponse;
                })
                .collect(Collectors.toList());
    }

    //Xoá sản phẩm
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(String id){
        Product product = productRepository.findById(id)
                        .orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        imageRepository.deleteAllByProductId(product.getId());
        productUnitRepository.deleteAllByProductId(id);
        productRepository.deleteById(product.getId());
    }
}
