package com.Microservices.inventoryService.exception;

public class ProductNotFoundException extends InventoryBusinessException{

    public  ProductNotFoundException(String productId){
        super("PRODUCT_NOT_FOUND", "product not found" +productId);
    }
}
