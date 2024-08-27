package com.project.pharmacy.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    CATEGORY_EXISTED("Category existed", 400, HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND("Category not found", 400, HttpStatus.BAD_REQUEST),
    PARENT_CATEGORY_NOT_FOUND("Parent Category not found", 400, HttpStatus.BAD_REQUEST),
    UNIT_EXISTED("Unit existed", 400, HttpStatus.BAD_REQUEST),
    UNIT_NOT_FOUND("Unit not found", 400, HttpStatus.BAD_REQUEST),
    COMPANY_EXISTED("Company existed", 400, HttpStatus.BAD_REQUEST),
    COMPANY_NOT_FOUND("Company not found", 400, HttpStatus.BAD_REQUEST),
    PRODUCT_EXISTED("Product existed", 400, HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND("Product not found", 400, HttpStatus.BAD_REQUEST),
    INVALID_VALIDATION_KEY("Invalid validation key", 400, HttpStatus.BAD_REQUEST),
    INVALID_COMPANY("Please fill out Company Name", 400, HttpStatus.BAD_REQUEST),
    INVALID_UNIT("Please fill out Unit Name", 400, HttpStatus.BAD_REQUEST),
    INVALID_CATEGORY("Please fill out Category Name", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT("Please fill out Product Name", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_PRICE("Please fill out Product Price", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_QUANTITY("Please fill out Product Quantity", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_BENEFITS("Please fill out Product Benefits", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_INGREDIENTS("Please fill out Product Ingredients", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_CONSTRAINDICATION("Please fill out Product Constraindication", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_OBJECT_USE("Please fill out Product Object Use", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_INSTRUCTION("Please fill out Product Instruction", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_PRESERVE("Please fill out Product Preserve", 400, HttpStatus.BAD_REQUEST),
    INVALID_PRODUCT_ADVICE("Please fill out Product Doctor Advice", 400, HttpStatus.BAD_REQUEST)
    ;
    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(String message, int code, HttpStatusCode statusCode) {
        this.message = message;
        this.code = code;
        this.statusCode = statusCode;
    }
}
