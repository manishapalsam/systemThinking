package com.Microservices.orderService.Entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.spi.LocaleNameProvider;

@Entity
@Table(name="orders")
public class Order {

    @Id
    private  String id;

    @Column(nullable = false)
    private String customerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus  status;

    @Column(nullable = false)
   private  Double totalAmount;



  /* 🔁 Cascade

    Cascade = “do the same thing to child automatically”

            👉 If you save / delete Order,
👉 OrderItems are saved / deleted automatically

Get Order
EAGER → Order + Items come together
LAZY  → Order first, Items later (on demand)
*/

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public Order(){}

    public Order(String id, String customerId, OrderStatus status, Double totalAmount, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
    }

    public  String getId(){
        return  id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }
    public  void setStatus(OrderStatus status){
        this.status = status;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }


    public  LocalDateTime getCreatedAt(){
        return  createdAt;
    }

    public  void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }


    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        FAILED,
        CANCELLED
    }
}
