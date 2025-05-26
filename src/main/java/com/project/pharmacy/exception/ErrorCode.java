package com.project.pharmacy.exception;

import lombok.Setter;
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
    PRODUCT_EXPIRATION_INVALID("Ngày hết hạn phải lớn hơn ngày tạo", 400, HttpStatus.BAD_REQUEST),

    // Price exception
    PRICE_EXISTED("Price existed", 400, HttpStatus.BAD_REQUEST),
    PRICE_NOT_FOUND("Price not found", 400, HttpStatus.BAD_REQUEST),
    PRICE_NOT_BE_EQUAL("Price not be equal", 400, HttpStatus.BAD_REQUEST),

    // User exception
    USER_EXISTED("Username existed", 400, HttpStatus.BAD_REQUEST),
    PASSWORD_EXISTED("Password existed", 400, HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH("Password does not match", 400, HttpStatus.BAD_REQUEST),
    PASSWORD_RE_ENTERING_INCORRECT("Mật khẩu không trùng hợp", 400, HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND("User not found", 400, HttpStatus.BAD_REQUEST),
    EMAIL_NOT_MATCH("Email không trùng khớp", 400, HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED("Địa chỉ email đã được sử dụng", 400, HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VERIFIED("Email chưa được xác thực. Vui lòng xác thực email!", 400, HttpStatus.BAD_REQUEST),
    PHONE_EXISTED("Số điện thoại đã tồn tại", 400, HttpStatus.BAD_REQUEST),

    //Employee
    EMPLOYEE_EXISTED("Employee existed", 400, HttpStatus.BAD_REQUEST),
    EMPLOYEE_NOT_FOUND("Employee not found", 400, HttpStatus.BAD_REQUEST),
    MISS_IMAGE("Vui lòng điền ảnh nhân viên", 400, HttpStatus.BAD_REQUEST),

    //Chat room
    CHAT_ROOM_NOT_FOUND("Chat room not found", 400, HttpStatus.BAD_REQUEST),

    // Cart and cart item Exception
    CART_EMPTY("Giỏ hàng trống!", 400, HttpStatus.BAD_REQUEST),
    CART_ITEM_NOT_FOUND("Cart item not found!", 400, HttpStatus.BAD_REQUEST),

    // Order exception
    UPDATE_ADDRESS("Vui lòng cập nhật địa chỉ mới!", 400, HttpStatus.BAD_REQUEST),
    ADDRESS_NOT_FOUND("Địa chỉ không tồn tại!", 400, HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND("Đơn hàng không tồn tại", 400, HttpStatus.BAD_REQUEST),

    //Coupon exception
    COUPON_NOT_FOUND("Mã giảm giá không tồn tại", 400, HttpStatus.BAD_REQUEST),
    COUPON_DONT_MATCH_ORDERREQUIRE("Đơn hàng không phù hợp với yêu cầu. Mua thêm: %s để có thể sử dụng mã giảm giá", 400, HttpStatus.BAD_REQUEST),

    //WHISTLIST
    WHISTLIST_NOT_FOUND("Whist list not found", 400, HttpStatus.BAD_REQUEST),
    WHISTLISTITEM_EXISTED("Sản phẩm đã tồn tại trong danh sách", 400, HttpStatus.BAD_REQUEST),

    //Image
    IMAGE_NOT_FOUND("Image not found", 400, HttpStatus.BAD_REQUEST),

    //Address exception
    ADDRESS_EXISTED("Địa chỉ đã tồn tại!", 400, HttpStatus.BAD_REQUEST),

    //Delivery exception
    DELIVERY_SERVICE_NOT_AVAILABLE("Khu vực không hỗ trợ giao hàng", 400, HttpStatus.BAD_REQUEST),

    //Payment
    PAYMENT_ERRROR("Thanh toán thất bại", 400, HttpStatus.BAD_REQUEST),

    //Feedback exception
    FEEDBACK_NOT_FOUND("Feedback not found!", 400, HttpStatus.BAD_REQUEST),
    DONT_FEEDBACK("Bạn vui lòng mua hàng để thực hiện đánh giá sản phẩm!", 400, HttpStatus.BAD_REQUEST),
    USER_DONT_FEEDBACK("Đây không phải feedback của bạn!", 400, HttpStatus.BAD_REQUEST),
    PRODUCT_DONT_FEEDBACK("Sản phẩm này chưa có đánh giá nào!", 400, HttpStatus.BAD_REQUEST),

    // Role exception
    ROLE_EXISTED("Role existed", 400, HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND("Role not found", 400, HttpStatus.BAD_REQUEST),

    // Login exception
    USER_NOT_EXISTED("Tên đăng nhập không tồn tại", 400, HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT("Mật khẩu không chính xác", 400, HttpStatus.BAD_REQUEST),
    USER_HAS_BEEN_BAN("User has been banned", 403, HttpStatus.UNAUTHORIZED),

    // Logout exception
    UNAUTHENTICATED("Unauthenticated", 400, HttpStatus.BAD_REQUEST),

    // OTP Exception
    OTP_EXPIRED("Mã OTP đã hết hạn", 400, HttpStatus.BAD_REQUEST),
    OTP_INCORRECT("Mã OTP không chính xác", 400, HttpStatus.BAD_REQUEST),

    // Price exception
    PRICE_NOT_ZERO("Giá phải lớn hơn 0", 400, HttpStatus.BAD_REQUEST),

    // Valid RequirePart
    MISSING_PART("Missing required part", 400, HttpStatus.BAD_REQUEST),

    // File
    FILE_SIZE_EXCEEDED("File phải nhỏ hơn 5MB", 400, HttpStatus.BAD_REQUEST),
    EMPTY_FILE("Vui lòng đính kèm hình ảnh", 500, HttpStatus.INTERNAL_SERVER_ERROR);

    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(String message, int code, HttpStatusCode statusCode) {
        this.message = message;
        this.code = code;
        this.statusCode = statusCode;
    }
}
