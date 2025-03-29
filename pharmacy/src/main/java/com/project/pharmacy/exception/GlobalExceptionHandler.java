package com.project.pharmacy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import com.project.pharmacy.dto.response.ApiResponse;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handlingRunTimeException(RuntimeException exception) {

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(400);
        apiResponse.setMessage(exception.getMessage());

        return ResponseEntity.status(400)
                .body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());

        // Sử dụng customMessage nếu có, nếu không thì lấy message mặc định từ ErrorCode
        apiResponse.setMessage(exception.getCustomMessage() != null
                ? exception.getCustomMessage()
                : errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidException(MethodArgumentNotValidException exception) {
        String defaultMessage = exception.getFieldError().getDefaultMessage();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(HttpStatus.BAD_REQUEST.value());
        apiResponse.setMessage(defaultMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
    }

    @ExceptionHandler(value = MissingServletRequestPartException.class)
    ResponseEntity<ApiResponse> handlingValidException(MissingServletRequestPartException exception) {
        ErrorCode errorCode = ErrorCode.MISSING_PART;

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage("Required part '" + exception.getRequestPartName() + "' is not present");

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    ResponseEntity<ApiResponse> handleMaxSizeException(MaxUploadSizeExceededException exception) {
        ErrorCode errorCode = ErrorCode.FILE_SIZE_EXCEEDED;

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }
}
