package com.pearchInventory.ioa.enums;

public enum ResponseCode {
    SUCCESS("200", "Operation successful"),
    BAD_REQUEST("400", "Bad request"),
    NOT_FOUND("404", "Resource not found"),
    INTERNAL_ERROR("500", "Internal server error"),
    USER_NOT_FOUND("10", "User not found"),
    EMAIL_EXISTS("09", "Email already exists"),
    INSUFFICIENT_BALANCE("05", "Insufficient balance"),
    ACCOUNT_ALREADY_EXISTS("02", "Account already exists"),
    ACCOUNT_NOT_FOUND("08", "Account not found"),
    CUSTOMER_NOT_FOUND("11", "Customer not found"),
    PRODUCT_NOT_FOUND("12", "Product not found"),
    INVALID_CREDENTIALS("400", "Invalid credentials");

    private final String code;
    private final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}