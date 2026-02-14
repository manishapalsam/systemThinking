package com.Microservices.orderService.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderRequest {

    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @Valid
    @NotEmpty(message = "Items cannot be empty")
    private  List<OrderItemRequest> items;


    @NotBlank(message = "Payment method is required")
    private String paymentMethod;


    public  OrderRequest(){}

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }


    public  static  class OrderItemRequest{
        @NotBlank(message = "Product ID is required")
        private  String productId;

        @Min(value = 1, message = "Quantity must be greater than zero")
        @NotNull(message = "Quantity is required")
        private Integer quantity;

        public OrderItemRequest() {}
        public OrderItemRequest(String productId, Integer quantity) {
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
