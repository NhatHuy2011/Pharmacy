package com.project.pharmacy.exception;

public class AppException extends RuntimeException {
    private ErrorCode errorCode;
    private String customMessage;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.customMessage = errorCode.getMessage(); // Lấy message mặc định
    }

    public AppException(ErrorCode errorCode, String customMessage) {
        super(customMessage); // Dùng thông báo lỗi tùy chỉnh
        this.errorCode = errorCode;
        this.customMessage = customMessage;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getCustomMessage() {
        return customMessage;
    }
}
