package com.Microservices.orderService.dto;



//         ✔ Don’t expose full Order
//        ✔ Faster payload
//        ✔ Matches API spec exactly
public class OrderSummaryResponse {
    private String orderId;
    private String status;

    public OrderSummaryResponse(String orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }
}
