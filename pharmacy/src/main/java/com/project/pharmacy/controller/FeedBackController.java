package com.project.pharmacy.controller;

import com.project.pharmacy.dto.request.CreateFeedBackRequest;
import com.project.pharmacy.dto.request.GetFeedBackByProductRequest;
import com.project.pharmacy.dto.request.UpdateFeedbackRequest;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.FeedBackResponse;
import com.project.pharmacy.service.FeedBackService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedBackController {
    FeedBackService feedBackService;

    //For USER
    @PostMapping
    public ApiResponse<FeedBackResponse> createFeedback(@RequestBody CreateFeedBackRequest request) {
        return ApiResponse.<FeedBackResponse>builder()
                .result(feedBackService.createFeedBackForUser(request))
                .build();
    }

    @PutMapping()
    public ApiResponse<FeedBackResponse> updateFeedBack(@RequestBody UpdateFeedbackRequest request){
        return ApiResponse.<FeedBackResponse>builder()
                .result(feedBackService.updateFeedback(request))
                .build();
    }

    @DeleteMapping("/user/{id}")
    public ApiResponse<FeedBackResponse> deleteFeedBackForUser(@PathVariable String id){
        feedBackService.deleteFeedbackForUser(id);
        return ApiResponse.<FeedBackResponse>builder()
                .message("Delete FeedBack Successful")
                .build();
    }

    //For EMPLOYEE
    @PostMapping("/employee")
    public ApiResponse<FeedBackResponse> createFeedbackForAdminAndEmployee(@RequestBody CreateFeedBackRequest request) {
        return ApiResponse.<FeedBackResponse>builder()
                .result(feedBackService.createFeedBackForEmployee(request))
                .build();
    }

    @GetMapping("/employee")
    public ApiResponse<List<FeedBackResponse>> getAll(){
        return ApiResponse.<List<FeedBackResponse>>builder()
                .result(feedBackService.getAll())
                .build();
    }

    @DeleteMapping("/employee/{id}")
    public ApiResponse<FeedBackResponse> deleteFeedBackForEmployeeAndAdmin(@PathVariable String id){
        feedBackService.deleteFeedbackForEmployee(id);
        return ApiResponse.<FeedBackResponse>builder()
                .message("Delete FeedBack Successful")
                .build();
    }

    //For ALL
    //Xem feedback goc
    @GetMapping("/null/{id}")
    public ApiResponse<List<FeedBackResponse>> getFeedBackByProduct(@PathVariable String id){
        return ApiResponse.<List<FeedBackResponse>>builder()
                .result(feedBackService.getFeedBackByProduct(id))
                .build();
    }

    //Xem reply feedback
    @GetMapping("{parentId}")
    public ApiResponse<List<FeedBackResponse>> getReplyFeedBack(@PathVariable String parentId){
        return ApiResponse.<List<FeedBackResponse>>builder()
                .result(feedBackService.getReplyFeedBack(parentId))
                .build();
    }
}
