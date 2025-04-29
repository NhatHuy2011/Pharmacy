package com.project.pharmacy.service;

import com.project.pharmacy.dto.response.CompanyResponse;
import com.project.pharmacy.dto.response.PriceResponse;
import com.project.pharmacy.dto.response.ProductResponse;
import com.project.pharmacy.dto.response.UnitResponse;
import com.project.pharmacy.entity.Image;
import com.project.pharmacy.entity.Price;
import com.project.pharmacy.entity.Product;
import com.project.pharmacy.mapper.ProductMapper;
import com.project.pharmacy.repository.CompanyRepository;
import com.project.pharmacy.repository.ImageRepository;
import com.project.pharmacy.repository.PriceRepository;
import com.project.pharmacy.repository.ProductRepository;
import com.project.pharmacy.dto.response.ProductBestSellerResponse;
import com.project.pharmacy.dto.response.TopCompanyResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HomeUserService {
    ProductRepository productRepository;

    ImageRepository imageRepository;

    PriceRepository priceRepository;

    ProductMapper productMapper;

    CompanyRepository companyRepository;
    //10 New Product
    public List<ProductResponse> getTop10NewProduct(){
        return productRepository.findTop10NewProduct().stream()
                .map(product -> {
                    Image firstImage = imageRepository.findFirstByProductId(product.getId());
                    String url = firstImage.getSource();

                    List<Price> prices = priceRepository.findByProductId(product.getId());

                    List<PriceResponse> priceResponses = prices.stream()
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
                            .toList();

                    ProductResponse productResponse = new ProductResponse();
                    productResponse.setId(product.getId());
                    productResponse.setName(product.getName());
                    productResponse.setImage(url);
                    productResponse.setPrices(priceResponses);

                    return productResponse;
                })
                .toList();
    }

    //Top 10 BestSeller
    public List<ProductResponse> getTop10ProductBestSeller(){
        List<ProductBestSellerResponse> top10 = productRepository.findTop10ProductBestSeller();

        //Lay danh sach id cua 10 san ban chay nhat
        List<String> productIds = top10.stream()
                .map(ProductBestSellerResponse::getProductId)
                .toList();

        //Dem so luong san pham da ban
        Map<String, Long> productSalesMap = top10.stream()
                .collect(Collectors.toMap(ProductBestSellerResponse::getProductId, ProductBestSellerResponse::getTotalQuantity));

        List<Product> products = productRepository.findProductsByIds(productIds);

        List<ProductResponse> productResponses = products.stream()
                .map(product -> {
                    Image firstImage = imageRepository.findFirstByProductId(product.getId());
                    String url = firstImage.getSource();

                    List<Price> prices = priceRepository.findByProductId(product.getId());

                    List<PriceResponse> priceResponses = prices.stream()
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
                            .toList();

                    ProductResponse productResponse = new ProductResponse();
                    productResponse.setId(product.getId());
                    productResponse.setName(product.getName());
                    productResponse.setImage(url);
                    productResponse.setPrices(priceResponses);

                    productResponse.setTotalSold(productSalesMap.get(product.getId()));

                    return productResponse;
                })
                .toList();

        return productResponses;
    }

    public List<CompanyResponse> getTop10Company() {
        List<TopCompanyResponse> topCompanies = companyRepository.getTop10Company();

        // Map TopCompany sang CompanyResponse
        return topCompanies.stream().map(topCompanyResponse -> {
            CompanyResponse response = new CompanyResponse();
            response.setId(topCompanyResponse.getId());
            response.setName(topCompanyResponse.getName());
            response.setImage(topCompanyResponse.getImage());
            return response;
        }).collect(Collectors.toList());
    }

}
