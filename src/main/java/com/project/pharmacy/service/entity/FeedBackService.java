package com.project.pharmacy.service.entity;

import com.project.pharmacy.dto.request.feedback.CreateFeedBackRequest;
import com.project.pharmacy.dto.request.feedback.UpdateFeedbackRequest;
import com.project.pharmacy.dto.response.entity.FeedBackResponse;
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

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedBackService {
    FeedBackRepository feedBackRepository;

    UserRepository userRepository;

    ProductRepository productRepository;

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
                    if (orders.getStatus().equals(OrderStatus.SUCCESS) || orders.getIsConfirm()) {
                        hasPurchased = true;
                        break;
                    }
                }
            }
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

    public List<FeedBackResponse> getFeedBackByUser(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return feedBackRepository.findAllByUser(user).stream()
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
        FeedBack parent = null;
        if(request.getParent() != null){
            parent = feedBackRepository.findById(request.getParent())
                    .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        FeedBack feedBack = FeedBack.builder()
                .user(null)
                .product(product)
                .feedback(request.getFeedback())
                .createDate(LocalDate.now())
                .parent(parent)
                .build();
        feedBackRepository.save(feedBack);

        FeedBackResponse.FeedBackResponseBuilder responseBuilder = FeedBackResponse.builder()
                .id(feedBack.getId())
                .productId(feedBack.getProduct().getId())
                .productName(feedBack.getProduct().getName())
                .feedback(feedBack.getFeedback())
                .createDate(feedBack.getCreateDate());

        // Kiểm tra null cho parent
        if (feedBack.getParent() != null) {
            responseBuilder.parent(FeedBackResponse.builder()
                    .id(feedBack.getParent().getId())
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
                            .userId(feedBack.getUser() != null ? feedBack.getUser().getId() : null)
                            .username(feedBack.getUser() != null ? feedBack.getUser().getUsername() : null)
                            .avatar(feedBack.getUser() != null ? feedBack.getUser().getImage() : null)
                            .productId(feedBack.getProduct().getId())
                            .productName(feedBack.getProduct().getName())
                            .feedback(feedBack.getFeedback())
                            .createDate(feedBack.getCreateDate());

                    // Kiểm tra null cho parent
                    if (feedBack.getParent() != null) {
                        responseBuilder.parent(FeedBackResponse.builder()
                                .id(feedBack.getParent().getId())
                                .userId(feedBack.getParent().getUser() != null ? feedBack.getParent().getUser().getId() : null)
                                .username(feedBack.getParent().getUser() != null ? feedBack.getParent().getUser().getUsername() : null)
                                .avatar(feedBack.getParent().getUser() != null ? feedBack.getParent().getUser().getImage() : null)
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

    //FOR USER AND EMPLOYEE
    @PreAuthorize("hasRole('USER') or hasRole('EMPLOYEE')")
    public FeedBackResponse updateFeedback(UpdateFeedbackRequest request) {
        FeedBack feedBack = feedBackRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.FEEDBACK_NOT_FOUND));

        feedBack.setFeedback(request.getFeedback());
        feedBackRepository.save(feedBack);

        FeedBackResponse.FeedBackResponseBuilder responseBuilder = FeedBackResponse.builder()
                .id(feedBack.getId())
                .userId(feedBack.getUser() != null ? feedBack.getUser().getId() : null)
                .username(feedBack.getUser() != null ? feedBack.getUser().getUsername() : null)
                .avatar(feedBack.getUser() != null ? feedBack.getUser().getImage() : null)
                .productId(feedBack.getProduct().getId())
                .productName(feedBack.getProduct().getName())
                .feedback(feedBack.getFeedback())
                .createDate(feedBack.getCreateDate());

        // Kiểm tra null cho parent
        if (feedBack.getParent() != null) {
            responseBuilder.parent(FeedBackResponse.builder()
                    .id(feedBack.getParent().getId())
                    .userId(feedBack.getParent().getUser() != null ? feedBack.getParent().getUser().getId() : null)
                    .username(feedBack.getParent().getUser() != null ? feedBack.getParent().getUser().getUsername() : null)
                    .avatar(feedBack.getParent().getUser() != null ? feedBack.getParent().getUser().getImage() : null)
                    .feedback(feedBack.getParent().getFeedback())
                    .createDate(feedBack.getParent().getCreateDate())
                    .build());
        }

        return responseBuilder.build();
    }

    //For ALL
    //Xem feedback goc
    public List<FeedBackResponse> getFeedBackByProduct(String id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        List<FeedBack> feedBacks = feedBackRepository.findAllByProductAndParent(product, null)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_DONT_FEEDBACK));

        return feedBacks.stream()
                .map(feedBack -> {
                    return FeedBackResponse.builder()
                            .id(feedBack.getId())
                            .userId(feedBack.getUser() != null ? feedBack.getUser().getId() : null)
                            .username(feedBack.getUser() != null ? feedBack.getUser().getUsername() : null)
                            .avatar(feedBack.getUser() != null ? feedBack.getUser().getImage() : null)
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
                            .userId(feedBack.getUser() != null ? feedBack.getUser().getId() : null)
                            .username(feedBack.getUser() != null ? feedBack.getUser().getUsername() : null)
                            .avatar(feedBack.getUser() != null ? feedBack.getUser().getImage() : null)
                            .productId(feedBack.getProduct().getId())
                            .productName(feedBack.getProduct().getName())
                            .feedback(feedBack.getFeedback())
                            .createDate(feedBack.getCreateDate());

                    // Kiểm tra null cho parent
                    if (feedBack.getParent() != null) {
                        responseBuilder.parent(FeedBackResponse.builder()
                                .id(feedBack.getParent().getId())
                                .userId(feedBack.getParent().getUser() != null ? feedBack.getParent().getUser().getId() : null)
                                .username(feedBack.getParent().getUser() != null ? feedBack.getParent().getUser().getUsername() : null)
                                .avatar(feedBack.getParent().getUser() != null ? feedBack.getParent().getUser().getImage() : null)
                                .feedback(feedBack.getParent().getFeedback())
                                .createDate(feedBack.getParent().getCreateDate())
                                .build());
                    }

                    return responseBuilder.build();
                })
                .toList();
    }
}
