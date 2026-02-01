package com.Microservices.paymentService.dto;

import java.time.LocalDateTime;

public class PaymentResponse {
 private String transactionId;
 private String orderId;
 private Double amount;
 private String method;
 private  String status;
 private LocalDateTime processedAt;

 public  PaymentResponse(){}
    public PaymentResponse(String transactionId, String orderId, Double amount, String method, String status, LocalDateTime processedAt) {
        this.transactionId = transactionId;
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.processedAt = processedAt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
