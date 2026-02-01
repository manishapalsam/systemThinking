package com.Microservices.orderService.dto;

public class PaymentRequest {
    private String orderId;
    private Double amount;
    private String method;

    public  PaymentRequest(){}

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

    public PaymentRequest(String orderId, Double amount, String method) {
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
    }


}
