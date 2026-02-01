package com.Microservices.orderService.Entity;

import jakarta.persistence.*;


@Entity
@Table(name = "order_items")
public class OrderItem {


    @Id
    private String id;


    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private  Order order;


    @Column(nullable = false)
    private  String productId;


    @Column(nullable = false)
    private  Integer quantity;


    @Column(nullable = false)
    private Double price;

    public OrderItem(){}

    public OrderItem(String id, Order order, String productId, Integer quantity, Double price) {
        this.id = id;
        this.order = order;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
