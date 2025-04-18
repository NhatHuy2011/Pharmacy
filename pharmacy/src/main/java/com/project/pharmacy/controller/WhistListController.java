package com.project.pharmacy.controller;

import com.project.pharmacy.dto.request.AddToWhistListRequest;
import com.project.pharmacy.dto.response.ApiResponse;
import com.project.pharmacy.dto.response.WhistListResponse;
import com.project.pharmacy.service.WhistListService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/whistlist")
public class WhistListController {
    WhistListService whistListService;

    @PostMapping
    public ApiResponse<WhistListResponse> addWhistList(@RequestBody AddToWhistListRequest request){
        return ApiResponse.<WhistListResponse>builder()
                .result(whistListService.addToWhistList(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<WhistListResponse>> getWhistList(){
        return ApiResponse.<List<WhistListResponse>>builder()
                .result(whistListService.getWhistList())
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<Void> deleteItemWhistList(@PathVariable String id){
        whistListService.deleteItemWhistList(id);
        return ApiResponse.<Void>builder()
                .message("Delete Item Successful")
                .build();
    }
}
