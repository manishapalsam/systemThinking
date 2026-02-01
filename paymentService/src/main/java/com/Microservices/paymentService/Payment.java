package com.Microservices.paymentService;


import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name="payments")
public class Payment {

    @Id
    public String id;


    @Column(nullable = false)
    private String orderId;


    @Column(nullable = false)
    private  Double amount;


    @Column(nullable = false)
    private String method;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime processedAt;

    public Payment(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }


    public  enum PaymentStatus{
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }
}
