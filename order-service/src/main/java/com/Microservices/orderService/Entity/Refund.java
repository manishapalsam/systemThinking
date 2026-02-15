package com.Microservices.orderService.Entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refunds",
        uniqueConstraints  = {
        @UniqueConstraint(columnNames = {"order_id"})
        }
)
public class Refund {

    @Id
    private String id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private  RefundStatus status;


    @Column(nullable = false)
    private LocalDateTime createdAt;


    public Refund(){}

    public Refund(String id, Order order, RefundStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.order = order;
        this.status = status;
        this.createdAt = createdAt;
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

    public RefundStatus getStatus() {
        return status;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public enum RefundStatus {
        INITIATED,
        SUCCESS,
        FAILED
    }

}




