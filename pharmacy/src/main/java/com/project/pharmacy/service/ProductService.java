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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    //Thêm sản phẩm
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request, List<MultipartFile> multipartFiles) throws IOException {
        Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(()-> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        Company company = companyRepository.findById(request.getCompanyId()).orElseThrow(()->new AppException(ErrorCode.COMPANY_NOT_FOUND));

        if(productRepository.existsByName(request.getName())){
            throw new AppException(ErrorCode.PRODUCT_EXISTED);
        }

        // Kiểm tra xem có ít nhất một tệp tin đính kèm hay không
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            throw new AppException(ErrorCode.EMPTY_FILE); // Bạn có thể định nghĩa mã lỗi này trong ErrorCode
        }

        //Tạo sản phẩm
        Product product = new Product();
        product.setName(request.getName());
        product.setQuantity(request.getQuantity());
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
        } else {
            throw new AppException(ErrorCode.EMPTY_FILE);
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .quantity(product.getQuantity())
                .benefits(product.getBenefits())
                .ingredients(product.getIngredients())
                .constraindication(product.getConstraindication())
                .object_use(product.getObject_use())
                .instruction(product.getInstruction())
                .preserve(product.getPreserve())
                .description(product.getDescription())
                .note(product.getNote())
                .doctor_advice(product.isDoctor_advice())
                .category(product.getCategory().getName())
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
            List<ProductUnit> productUnits = productUnitRepository.findByProductId(product.getId());
            List<Integer> price = new ArrayList<>();
            List<String> unit = new ArrayList<>();
            for(ProductUnit productUnit: productUnits){
                price.add(productUnit.getPrice());
                unit.add(productUnit.getUnit().getName());
            }
            ProductResponse productResponse = ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price1(price)
                    .unit1(unit)
                    .quantity(product.getQuantity())
                    .benefits(product.getBenefits())
                    .ingredients(product.getIngredients())
                    .constraindication(product.getConstraindication())
                    .object_use(product.getObject_use())
                    .instruction(product.getInstruction())
                    .preserve(product.getPreserve())
                    .description(product.getDescription())
                    .note(product.getNote())
                    .category(product.getCategory().getName())
                    .doctor_advice(product.isDoctor_advice())
                    .company(product.getCompany().getName())
                    .image(url)
                    .build();
            productResponses.add(productResponse);
        }
        return productResponses;
    }

    //Cập nhật sản phẩm
    @Transactional
    public ProductResponse updateProduct(ProductUpdateRequest request, List<MultipartFile> files) throws IOException{
        Product product = productRepository.findById(request.getId())
                .orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        //Cập nhật thông tin
        product.setName(request.getName());
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
                .quantity(product.getQuantity())
                .category(product.getCategory().getName())
                .benefits(product.getBenefits())
                .ingredients(product.getIngredients())
                .constraindication(product.getConstraindication())
                .object_use(product.getObject_use())
                .instruction(product.getInstruction())
                .preserve(product.getPreserve())
                .description(product.getDescription())
                .note(product.getNote())
                .doctor_advice(product.isDoctor_advice())
                .company(product.getCompany().getName())
                .images(imageUrls)
                .build();
    }

    //Lấy 1 sản phẩm
    public List<ProductResponse> getOne(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<String> imageUrls = images.stream()
                .map(Image::getSource)
                .collect(Collectors.toList());
        List<ProductUnit> productUnits = productUnitRepository.findByProductId(product.getId());

        List<ProductResponse> productResponses = new ArrayList<>();

        for (ProductUnit productUnit : productUnits) {
            ProductResponse response = ProductResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(productUnit.getPrice()) // Giá trị price là int
                    .quantity(product.getQuantity())
                    .category(product.getCategory().getName())
                    .benefits(product.getBenefits())
                    .ingredients(product.getIngredients())
                    .constraindication(product.getConstraindication())
                    .object_use(product.getObject_use())
                    .instruction(product.getInstruction())
                    .preserve(product.getPreserve())
                    .description(product.getDescription())
                    .note(product.getNote())
                    .unit(productUnit.getUnit().getName()) // Đơn vị của sản phẩm
                    .company(product.getCompany().getName())
                    .images(imageUrls)
                    .build();
            productResponses.add(response);
        }

        return productResponses;
    }


    //Xoá sản phẩm
    @Transactional
    public void deleteProduct(String id){
        Product product = productRepository.findById(id)
                        .orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        imageRepository.deleteAllByProductId(product.getId());
        productUnitRepository.deleteAllByProductId(id);
        productRepository.deleteById(product.getId());
    }
}
