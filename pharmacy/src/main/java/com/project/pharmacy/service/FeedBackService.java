package com.project.pharmacy.service;

import com.project.pharmacy.dto.request.CreateFeedBackRequest;
import com.project.pharmacy.dto.request.GetFeedBackByProductRequest;
import com.project.pharmacy.dto.request.UpdateFeedbackRequest;
import com.project.pharmacy.dto.response.FeedBackResponse;
import com.project.pharmacy.entity.*;
import com.project.pharmacy.enums.OrderStatus;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedBackService {
    FeedBackRepository feedBackRepository;

    UserRepository userRepository;

    ProductRepository productRepository;

    OrderRepository orderRepository;

    OrderItemRepository orderItemRepository;

    //For USER
    public FeedBackResponse createFeedBackForUser(CreateFeedBackRequest request){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean hasPurchased = false;

        for (Orders orders : user.getOrders()) {
            for (OrderItem orderItem : orders.getOrderItems()) {
                if (orderItem.getPrice().getId().equals(request.getPriceId())) {
                    if (orders.getStatus().equals(OrderStatus.PENDING) || orders.getStatus().equals(OrderStatus.FAILED)) {
                        throw new AppException(ErrorCode.DONT_FEEDBACK);
                    }
                    hasPurchased = true;
                    break;
                }
            }
            if (hasPurchased) break;
        }

        if (!hasPurchased) {
            throw new AppException(ErrorCode.DONT_FEEDBACK);
        }

        FeedBack parent = null;
        if(request.getParent() != null){
            parent = feedBackRepository.findById(request.getParent())
                    .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        FeedBack feedBack = FeedBack.builder()
                .user(user)
                .product(product)
                .feedback(request.getFeedback())
                .createDate(LocalDate.now())
                .parent(parent)
                .build();
        feedBackRepository.save(feedBack);

        FeedBackResponse.FeedBackResponseBuilder responseBuilder = FeedBackResponse.builder()
                .id(feedBack.getId())
                .userId(feedBack.getUser().getId())
                .username(feedBack.getUser().getUsername())
                .avatar(feedBack.getUser().getImage())
                .productId(feedBack.getProduct().getId())
                .productName(feedBack.getProduct().getName())
                .feedback(feedBack.getFeedback())
                .createDate(feedBack.getCreateDate());

        // Kiểm tra null cho parent
        if (feedBack.getParent() != null) {
            responseBuilder.parent(FeedBackResponse.builder()
                    .id(feedBack.getParent().getId())
                    .userId(feedBack.getParent().getUser().getId())
                    .username(feedBack.getParent().getUser().getUsername())
                    .avatar(feedBack.getParent().getUser().getImage())
                    .feedback(feedBack.getParent().getFeedback())
                    .createDate(feedBack.getParent().getCreateDate())
                    .build());
        }

        return responseBuilder.build();
    }

    public FeedBackResponse updateFeedback(UpdateFeedbackRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        FeedBack feedBack = user.getFeedBacks().stream()
                .filter(feedBack1 -> feedBack1.getId().equals(request.getId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_DONT_FEEDBACK));

        feedBack.setFeedback(request.getFeedback());
        feedBackRepository.save(feedBack);

        FeedBackResponse.FeedBackResponseBuilder responseBuilder = FeedBackResponse.builder()
                .id(feedBack.getId())
                .userId(feedBack.getUser().getId())
                .username(feedBack.getUser().getUsername())
                .avatar(feedBack.getUser().getImage())
                .productId(feedBack.getProduct().getId())
                .productName(feedBack.getProduct().getName())
                .feedback(feedBack.getFeedback())
                .createDate(feedBack.getCreateDate());

        // Kiểm tra null cho parent
        if (feedBack.getParent() != null) {
            responseBuilder.parent(FeedBackResponse.builder()
                    .id(feedBack.getParent().getId())
                    .userId(feedBack.getParent().getUser().getId())
                    .username(feedBack.getParent().getUser().getUsername())
                    .avatar(feedBack.getParent().getUser().getImage())
                    .feedback(feedBack.getParent().getFeedback())
                    .createDate(feedBack.getParent().getCreateDate())
                    .build());
        }

        return responseBuilder.build();
    }

    public void deleteFeedbackForUser(String id){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        FeedBack feedBack = user.getFeedBacks().stream()
                .filter(feedBack1 -> feedBack1.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_DONT_FEEDBACK));

        deleteFeedbackRecursive(feedBack);
    }

    protected void deleteFeedbackRecursive(FeedBack feedBack) {
        List<FeedBack> feedBacks = feedBackRepository.findByParent(feedBack);
        for (FeedBack subFeedBack : feedBacks) {
            deleteFeedbackRecursive(subFeedBack);
        }
        feedBackRepository.delete(feedBack);
    }

    //For ADMIN and EMPLOYEE
    @PreAuthorize("hasRole('EMPLOYEE')")
    public FeedBackResponse createFeedBackForEmployee(CreateFeedBackRequest request){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        FeedBack parent = null;
        if(request.getParent() != null){
            parent = feedBackRepository.findById(request.getParent())
                    .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        FeedBack feedBack = FeedBack.builder()
                .user(user)
                .product(product)
                .feedback(request.getFeedback())
                .createDate(LocalDate.now())
                .parent(parent)
                .build();
        feedBackRepository.save(feedBack);

        FeedBackResponse.FeedBackResponseBuilder responseBuilder = FeedBackResponse.builder()
                .id(feedBack.getId())
                .userId(feedBack.getUser().getId())
                .username(feedBack.getUser().getUsername())
                .avatar(feedBack.getUser().getImage())
                .productId(feedBack.getProduct().getId())
                .productName(feedBack.getProduct().getName())
                .feedback(feedBack.getFeedback())
                .createDate(feedBack.getCreateDate());

        // Kiểm tra null cho parent
        if (feedBack.getParent() != null) {
            responseBuilder.parent(FeedBackResponse.builder()
                    .id(feedBack.getParent().getId())
                    .userId(feedBack.getParent().getUser().getId())
                    .username(feedBack.getParent().getUser().getUsername())
                    .avatar(feedBack.getParent().getUser().getImage())
                    .feedback(feedBack.getParent().getFeedback())
                    .createDate(feedBack.getParent().getCreateDate())
                    .build());
        }

        return responseBuilder.build();
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    public List<FeedBackResponse> getAll(){
        return feedBackRepository.findAll().stream()
                .map(feedBack -> {
                    FeedBackResponse.FeedBackResponseBuilder responseBuilder = FeedBackResponse.builder()
                            .id(feedBack.getId())
                            .userId(feedBack.getUser().getId())
                            .username(feedBack.getUser().getUsername())
                            .avatar(feedBack.getUser().getImage())
                            .productId(feedBack.getProduct().getId())
                            .productName(feedBack.getProduct().getName())
                            .feedback(feedBack.getFeedback())
                            .createDate(feedBack.getCreateDate());

                    // Kiểm tra null cho parent
                    if (feedBack.getParent() != null) {
                        responseBuilder.parent(FeedBackResponse.builder()
                                .id(feedBack.getParent().getId())
                                .userId(feedBack.getParent().getUser().getId())
                                .username(feedBack.getParent().getUser().getUsername())
                                .avatar(feedBack.getParent().getUser().getImage())
                                .feedback(feedBack.getParent().getFeedback())
                                .createDate(feedBack.getParent().getCreateDate())
                                .build());
                    }

                    return responseBuilder.build();
                })
                .toList();
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    public void deleteFeedbackForEmployee(String id){
        FeedBack feedBack = feedBackRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));

        deleteFeedbackRecursive(feedBack);
    }

    //For ALL
    //Xem feedback goc
    public List<FeedBackResponse> getFeedBackByProduct(GetFeedBackByProductRequest request){
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        List<FeedBack> feedBacks = feedBackRepository.findAllByProductAndParent(product, null)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_DONT_FEEDBACK));

        return feedBacks.stream()
                .map(feedBack -> {
                    return FeedBackResponse.builder()
                            .id(feedBack.getId())
                            .userId(feedBack.getUser().getId())
                            .username(feedBack.getUser().getUsername())
                            .avatar(feedBack.getUser().getImage())
                            .productId(product.getId())
                            .productName(product.getName())
                            .feedback(feedBack.getFeedback())
                            .createDate(feedBack.getCreateDate())
                            .build();
                })
                .toList();
    }

    //Xem feedback reply
    public List<FeedBackResponse> getReplyFeedBack(String parentId){
        FeedBack parent = feedBackRepository.findById(parentId)
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));

        return feedBackRepository.findByParent(parent).stream()
                .map(feedBack -> {
                    FeedBackResponse.FeedBackResponseBuilder responseBuilder = FeedBackResponse.builder()
                            .id(feedBack.getId())
                            .userId(feedBack.getUser().getId())
                            .username(feedBack.getUser().getUsername())
                            .avatar(feedBack.getUser().getImage())
                            .productId(feedBack.getProduct().getId())
                            .productName(feedBack.getProduct().getName())
                            .feedback(feedBack.getFeedback())
                            .createDate(feedBack.getCreateDate());

                    // Kiểm tra null cho parent
                    if (feedBack.getParent() != null) {
                        responseBuilder.parent(FeedBackResponse.builder()
                                .id(feedBack.getParent().getId())
                                .userId(feedBack.getParent().getUser().getId())
                                .username(feedBack.getParent().getUser().getUsername())
                                .avatar(feedBack.getParent().getUser().getImage())
                                .feedback(feedBack.getParent().getFeedback())
                                .createDate(feedBack.getParent().getCreateDate())
                                .build());
                    }

                    return responseBuilder.build();
                })
                .toList();
    }
}