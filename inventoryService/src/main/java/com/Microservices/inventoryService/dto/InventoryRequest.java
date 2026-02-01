package com.Microservices.inventoryService.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class InventoryRequest {


    @NotEmpty(message = "Items cannot be empty")
    private List<InventoryItemRequest> items;

    public InventoryRequest() {}

    public InventoryRequest(List<InventoryItemRequest> items) {
        this.items = items;
    }

    public List<InventoryItemRequest> getItems() {
        return items;
    }

    public void setItems(List<InventoryItemRequest> items) {
        this.items = items;
    }

    public static class InventoryItemRequest {

        @jakarta.validation.constraints.NotBlank(message = "Product ID is required")
    private String productId;


    @NotNull(message = "Quantity is required")
    private Integer quantity;

    public InventoryItemRequest() {}

    public InventoryItemRequest(String productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}

}
