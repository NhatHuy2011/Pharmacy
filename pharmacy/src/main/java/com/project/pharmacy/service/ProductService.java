package com.project.pharmacy.service;

import com.project.pharmacy.dto.request.ProductCreateRequest;
import com.project.pharmacy.dto.request.ProductUpdateRequest;
import com.project.pharmacy.dto.response.ProductResponse;
import com.project.pharmacy.entity.*;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;

    CategoryRepository categoryRepository;

    CompanyRepository companyRepository;

    UnitRepository unitRepository;

    ImageService imageService;

    ImageRepository imageRepository;
    //Thêm sản phẩm
    public ProductResponse createProduct(ProductCreateRequest request, List<MultipartFile> multipartFiles) throws IOException {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(()-> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Company company = companyRepository.findById(request.getCompanyId()).orElseThrow(()->new AppException(ErrorCode.COMPANY_NOT_FOUND));

        Unit unit = unitRepository.findById(request.getUnitId()).orElseThrow(()->new AppException(ErrorCode.UNIT_NOT_FOUND));

        if(productRepository.existsByName(request.getName())){
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }

        //Tạo sản phẩm
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setUnit(unit);
        product.setCategory(category);
        product.setBenefits(request.getBenefits());
        product.setIngredients(request.getIngredients());
        product.setConstraindication(request.getConstraindication());
        product.setObject_use(request.getObject_use());
        product.setInstruction(request.getInstruction());
        product.setPreserve(request.getPreserve());
        product.setDescription(request.getDescription());
        product.setNote(request.getNote());
        product.setDoctor_advice(request.isDoctor_advice());
        product.setCompany(company);

        //Lưu sản phẩm
        productRepository.save(product);

        List<String> imageUrls = new ArrayList<>();
        //Lưu hình ảnh vào Cloudinary và lưu Url vào cơ sở dữ liệu
        if(multipartFiles != null && !multipartFiles.isEmpty()){
            for (MultipartFile multipartFile : multipartFiles) {
                String urlImage = imageService.uploadImage(multipartFile);
                imageUrls.add(urlImage);

                Image image = new Image();
                image.setProduct(product);
                image.setSource(urlImage);
                imageRepository.save(image);
            }
        }

        return ProductResponse.builder()
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .doctor_advice(product.isDoctor_advice())
                .category(product.getCategory().getName())
                .unit(product.getUnit().getName())
                .company(product.getCompany().getName())
                .images(imageUrls)
                .build();
    }

    //Xem danh sách sản phẩm
    public List<ProductResponse> getAllProduct(){
        List<Product> products = productRepository.findAll();

        List<ProductResponse> productResponses = new ArrayList<>();

        for(Product product: products){
            //Lay hinh anh dau tien
            Image firstImage = imageRepository.findFirstByProductId(product.getId());
            String url = firstImage != null ? firstImage.getSource() : null;

            ProductResponse productResponse = ProductResponse.builder()
                    .name(product.getName())
                    .price(product.getPrice())
                    .quantity(product.getQuantity())
                    .unit(product.getUnit()!=null ? product.getUnit().getName():null)
                    .category(product.getCategory()!=null ? product.getCategory().getName():null)
                    .doctor_advice(product.isDoctor_advice())
                    .company(product.getCompany()!=null ? product.getCompany().getName():null)
                    .image(url)
                    .build();
            productResponses.add(productResponse);
        }
        return productResponses;
    }

    //Cập nhật sản phẩm
    @Transactional
    public ProductResponse updateProduct(String id, ProductUpdateRequest request, List<MultipartFile> files) throws IOException{
        Product product = productRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        //Cập nhật thông tin
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setBenefits(request.getBenefits());
        product.setIngredients(request.getIngredients());
        product.setConstraindication(request.getConstraindication());
        product.setObject_use(request.getObject_use());
        product.setInstruction(request.getInstruction());
        product.setPreserve(request.getPreserve());
        product.setDescription(request.getDescription());
        product.setNote(request.getNote());
        product.setDoctor_advice(request.isDoctor_advice());

        // Cập nhật các mối quan hệ
        if (request.getUnitId() != null) {
            Unit unit = unitRepository.findById(request.getUnitId())
                    .orElseThrow(() -> new AppException(ErrorCode.UNIT_NOT_FOUND));
            product.setUnit(unit);
        }

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

        List<String> imageUrls = new ArrayList<>();
        // Kiểm tra nếu files != null thì mới thực hiện cập nhật hình ảnh
        if(files != null && !files.isEmpty()){
            // Xóa các hình ảnh cũ liên quan đến sản phẩm
            imageRepository.deleteAllByProductId(product.getId());
            for (MultipartFile multipartFile : files) {
                String urlImage = imageService.uploadImage(multipartFile);
                imageUrls.add(urlImage);

                Image image = new Image();
                image.setProduct(product);
                image.setSource(urlImage);
                imageRepository.save(image);
            }
        } else {
            // Truy vấn danh sách hình ảnh từ ImageRepository
            List<Image> images = imageRepository.findByProductId(product.getId());
            imageUrls = images.stream()
                              .map(Image::getSource)
                              .collect(Collectors.toList());
        }
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .unit(product.getUnit() != null ? product.getUnit().getName() : null)
                .category(product.getCategory() != null ? product.getCategory().getName() : null)
                .benefits(product.getBenefits())
                .ingredients(product.getIngredients())
                .constraindication(product.getConstraindication())
                .object_use(product.getObject_use())
                .instruction(product.getInstruction())
                .preserve(product.getPreserve())
                .description(product.getDescription())
                .note(product.getNote())
                .doctor_advice(product.isDoctor_advice())
                .company(product.getCompany() != null ? product.getCompany().getName() : null)
                .images(imageUrls)
                .build();
    }
    //Lấy 1 sản phẩm
    public ProductResponse getOne(String id){
        Product product = productRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<String> imageUrls = new ArrayList<>();
        imageUrls = images.stream()
                .map(Image::getSource)
                .collect(Collectors.toList());

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .unit(product.getUnit() != null ? product.getUnit().getName() : null)
                .category(product.getCategory() != null ? product.getCategory().getName() : null)
                .benefits(product.getBenefits())
                .ingredients(product.getIngredients())
                .constraindication(product.getConstraindication())
                .object_use(product.getObject_use())
                .instruction(product.getInstruction())
                .preserve(product.getPreserve())
                .description(product.getDescription())
                .note(product.getNote())
                .doctor_advice(product.isDoctor_advice())
                .company(product.getCompany() != null ? product.getCompany().getName() : null)
                .images(imageUrls)
                .build();
    }

    //Xoá sản phẩm
    @Transactional
    public void deleteProduct(String id){
        Product product = productRepository.findById(id)
                        .orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        imageRepository.deleteAllByProductId(product.getId());
        productRepository.deleteById(product.getId());
    }
}
