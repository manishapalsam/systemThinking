package com.Microservices.orderService.exception;




//409 STATUS
public class BusinessConflictException extends RuntimeException{

    private final String errorCode;

    public BusinessConflictException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
