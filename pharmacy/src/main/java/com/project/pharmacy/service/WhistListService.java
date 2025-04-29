package com.project.pharmacy.service;

import com.project.pharmacy.dto.request.AddToWhistListRequest;
import com.project.pharmacy.dto.response.WhistListResponse;
import com.project.pharmacy.entity.Product;
import com.project.pharmacy.entity.User;
import com.project.pharmacy.entity.WhistList;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.ProductRepository;
import com.project.pharmacy.repository.UserRepository;
import com.project.pharmacy.repository.WhistListRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WhistListService {
    WhistListRepository whistListRepository;

    UserRepository userRepository;

    ProductRepository productRepository;

    public WhistListResponse addToWhistList(AddToWhistListRequest request){
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if(whistListRepository.existsByUserAndProduct(user, product)){
            throw new AppException(ErrorCode.WHISTLISTITEM_EXISTED);
        }

        WhistList whistList = WhistList.builder()
                .user(user)
                .product(product)
                .build();

        whistListRepository.save(whistList);

        return WhistListResponse.builder()
                .id(whistList.getId())
                .userId(user.getId())
                .productId(product.getId())
                .productName(product.getName())
                .image(product.getImages().stream()
                        .findFirst()
                        .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_FOUND))
                        .getSource())
                .build();
    }

    public List<WhistListResponse> getWhistList(){
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        List<WhistList> whistLists = whistListRepository.findByUser(user);

        return whistLists.stream()
                .map(whistList -> {
                    return  WhistListResponse.builder()
                            .id(whistList.getId())
                            .userId(whistList.getUser().getId())
                            .productId(whistList.getProduct().getId())
                            .productName(whistList.getProduct().getName())
                            .image(whistList.getProduct().getImages().stream()
                                    .findFirst()
                                    .orElseThrow(() -> new AppException(ErrorCode.IMAGE_NOT_FOUND))
                                    .getSource())
                            .build();
                })
                .toList();
    }

    public void deleteItemWhistList(String id){
        WhistList whistList = whistListRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.WHISTLIST_NOT_FOUND));

        whistListRepository.delete(whistList);
    }
}
