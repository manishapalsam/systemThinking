package com.Microservices.inventoryService.entity;

import jakarta.persistence.*;

import java.nio.channels.InterruptedByTimeoutException;

@Entity
@Table(name= "inventory")
public class Inventory {
    @Id
    private String productId;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity;

    @Version
    private Long version; //OPTIMISTIC LOCKING
    public Inventory() {}

    public Inventory(String productId, Integer availableQuantity, Integer reservedQuantity) {
        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public Integer getAvailableStock() {
        //Thread.sleep(8000);
        return availableQuantity - reservedQuantity;
    }
}
