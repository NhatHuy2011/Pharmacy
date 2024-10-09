package com.project.pharmacy.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Category exeption
    CATEGORY_EXISTED("Category existed", 400, HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND("Category not found", 400, HttpStatus.BAD_REQUEST),
    PARENT_CATEGORY_NOT_FOUND("Parent Category not found", 400, HttpStatus.BAD_REQUEST),

    // Unit exception
    UNIT_EXISTED("Unit existed", 400, HttpStatus.BAD_REQUEST),
    UNIT_NOT_FOUND("Unit not found", 400, HttpStatus.BAD_REQUEST),

    // Company exception
    COMPANY_EXISTED("Company existed", 400, HttpStatus.BAD_REQUEST),
    COMPANY_NOT_FOUND("Company not found", 400, HttpStatus.BAD_REQUEST),

    // Product exception
    PRODUCT_EXISTED("Product existed", 400, HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND("Product not found", 400, HttpStatus.BAD_REQUEST),

    // Product and Unit exception
    PRICE_EXISTED("Price existed", 400, HttpStatus.BAD_REQUEST),
    PRICE_NOT_FOUND("Price not found", 400, HttpStatus.BAD_REQUEST),
    PRICE_NOT_BE_EQUAL("Price not be equal", 400, HttpStatus.BAD_REQUEST),

    // User exception
    USER_EXISTED("User existed", 400, HttpStatus.BAD_REQUEST),
    PASSWORD_EXISTED("Password existed", 400, HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH("Password does not match", 400, HttpStatus.BAD_REQUEST),
    PASSWORD_RE_ENTERING_INCORRECT("Mật khẩu không trùng hợp", 400, HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("User not found", 400, HttpStatus.BAD_REQUEST),
    EMAIL_NOT_MATCH("Email không trùng khớp", 400, HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED("Địa chỉ email đã được sử dụng", 400, HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VERIFIED("Email chưa được xác thực. Vui lòng xác thực email!", 400, HttpStatus.BAD_REQUEST),

    // Role exception
    ROLE_EXISTED("Role existed", 400, HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND("Role not found", 400, HttpStatus.BAD_REQUEST),

    // Permission exception
    PERMISSION_EXISTED("Permission existed", 400, HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_FOUND("Permission not found", 400, HttpStatus.BAD_REQUEST),

    // Login exception
    USER_NOT_EXISTED("Tên đăng nhập không tồn tại", 400, HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT("Mật khẩu không chính xác", 400, HttpStatus.BAD_REQUEST),
    USER_HAS_BEEN_BAN("User has been ban", 403, HttpStatus.UNAUTHORIZED),

    // Logout exception
    UNAUTHENTICATED("Unauthenticated", 400, HttpStatus.BAD_REQUEST),

    // OTP Exception
    OTP_EXPIRED("Mã OTP đã hết hạn", 400, HttpStatus.BAD_REQUEST),
    OTP_INCORRECT("Mã OTP không chính xác", 400, HttpStatus.BAD_REQUEST),

    // Price exception
    PRICE_NOT_ZERO("Price can not be less than zero", 400, HttpStatus.BAD_REQUEST),

    // Valid RequirePart
    MISSING_PART("Missing required part", 400, HttpStatus.BAD_REQUEST),

    // File
    FILE_SIZE_EXCEEDED("File phải nhỏ hơn 5MB", 400, HttpStatus.BAD_REQUEST),
    EMPTY_FILE("Vui lòng đính kèm hình ảnh", 500, HttpStatus.INTERNAL_SERVER_ERROR),

    // Data
    COLUMN_CANNOT_BE_NULL("Colume can not be null", 500, HttpStatus.INTERNAL_SERVER_ERROR);

    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(String message, int code, HttpStatusCode statusCode) {
        this.message = message;
        this.code = code;
        this.statusCode = statusCode;
    }
}
