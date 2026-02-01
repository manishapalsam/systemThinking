package com.Microservices.orderService.dto;

import java.util.List;

public class InventoryRequest {
    private List<InventoryItemRequest> items;

    public InventoryRequest(){}

    public InventoryRequest(List<InventoryItemRequest> items) {
        this.items = items;
    }

    public List<InventoryItemRequest> getItems() {
        return items;
    }

    public void setItems(List<InventoryItemRequest> items) {
        this.items = items;
    }
}
