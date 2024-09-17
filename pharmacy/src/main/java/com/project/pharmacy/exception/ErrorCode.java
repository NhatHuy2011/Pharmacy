package com.project.pharmacy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    //Category exeption
    CATEGORY_EXISTED("Category existed", 400, HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND("Category not found", 400, HttpStatus.BAD_REQUEST),
    PARENT_CATEGORY_NOT_FOUND("Parent Category not found", 400, HttpStatus.BAD_REQUEST),

    //Unit exception
    UNIT_EXISTED("Unit existed", 400, HttpStatus.BAD_REQUEST),
    UNIT_NOT_FOUND("Unit not found", 400, HttpStatus.BAD_REQUEST),

    //Company exception
    COMPANY_EXISTED("Company existed", 400, HttpStatus.BAD_REQUEST),
    COMPANY_NOT_FOUND("Company not found", 400, HttpStatus.BAD_REQUEST),

    //Product exception
    PRODUCT_EXISTED("Product existed", 400, HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND("Product not found", 400, HttpStatus.BAD_REQUEST),

    //Product and Unit exception
    PRODUCT_UNIT_EXISTED("Product and Unit existed", 400, HttpStatus.BAD_REQUEST),
    PRODUCT_UNIT_NOT_FOUND("Product and Unit not found", 400, HttpStatus.BAD_REQUEST),

    //User exception
    USER_EXISTED("User existed", 400, HttpStatus.BAD_REQUEST),

    //Login exception
    USER_NOT_EXISTED("Username not existed", 400, HttpStatus.BAD_REQUEST),
    PASSWORD_INCORRECT("Password is incorrect", 400, HttpStatus.BAD_REQUEST),

    //Logout exception
    UNAUTHENTICATED("Unauthenticated", 400, HttpStatus.BAD_REQUEST),

    //Exception relate to validation key
    INVALID_VALIDATION_KEY("Invalid validation key", 400, HttpStatus.BAD_REQUEST),

    //Valid Product
    INVALID_COMPANY_NAME("Please fill out Company Name", 400, HttpStatus.BAD_REQUEST),
    INVALID_COMPANY_ORIGIN("Please fill out Company Origin", 400, HttpStatus.BAD_REQUEST),
    INVALID_UNIT("Please fill out Unit Name", 400, HttpStatus.BAD_REQUEST),
    INVALID_CATEGORY("Please fill out Category Name", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_NAME("Please fill out Product Name", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_QUANTITY("Please fill out Product Quantity", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_BENEFITS("Please fill out Product Benefits", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_INGREDIENTS("Please fill out Product Ingredients", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_CONSTRAINDICATION("Please fill out Product Constraindication", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_OBJECT_USE("Please fill out Product Object Use", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_INSTRUCTION("Please fill out Product Instruction", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_PRESERVE("Please fill out Product Preserve", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_ADVICE("Please fill out Product Doctor Advice", 400, HttpStatus.BAD_REQUEST),

    //Valid User
    INVALID_USERNAME("Username must has at least 3 characters", 400, HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD("Password must has at least 8 characters", 400, HttpStatus.BAD_REQUEST),

    //Valid ProductUnit
    INVALID_PRODUCT_ID("Please fill out Product Info", 400, HttpStatus.BAD_REQUEST),
    INVALID_UNIT_ID("Please fill out Unit Info", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_PRICE("Please fill out Product Price", 400, HttpStatus.BAD_REQUEST),
    PRICE_NOT_ZERO("Price can not be less than zero", 400, HttpStatus.BAD_REQUEST),

    //Valid RequirePart
    MISSING_PART("Missing required part", 400, HttpStatus.BAD_REQUEST),

    //File
    FILE_SIZE_EXCEEDED("File size exceeds the maximum limit", 400, HttpStatus.BAD_REQUEST),
    EMPTY_FILE("Empty file", 400, HttpStatus.INTERNAL_SERVER_ERROR),

    //Data
    COLUMN_CANNOT_BE_NULL("Colume can not be null", 500 , HttpStatus.INTERNAL_SERVER_ERROR);

    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(String message, int code, HttpStatusCode statusCode) {
        this.message = message;
        this.code = code;
        this.statusCode = statusCode;
    }
}
