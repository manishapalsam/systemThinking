package org.systemthinking.payment_service2.exception;

public class PaymentServiceException extends  RuntimeException{
    public PaymentServiceException(String message){
        super(message);
    }
    public PaymentServiceException(String message, Throwable cause){
        super(message, cause);
    }
}

