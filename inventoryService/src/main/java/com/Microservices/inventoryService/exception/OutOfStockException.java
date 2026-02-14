package com.Microservices.inventoryService.exception;

public class OutOfStockException extends InventoryBusinessException {

    public OutOfStockException(String productId) {
        super("OUT_OF_STOCK",
                "Insufficient stock for product: " + productId);
    }
}

