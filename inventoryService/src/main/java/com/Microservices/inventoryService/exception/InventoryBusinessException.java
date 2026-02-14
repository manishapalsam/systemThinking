package com.Microservices.inventoryService.exception;

public class InventoryBusinessException extends  RuntimeException{

    private final String errorCode;

    public InventoryBusinessException(String errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode(){
        return  errorCode;
    }
}
