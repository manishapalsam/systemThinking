package com.Microservices.orderService.Controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/orders")
public class OrderController {


    private final RestTemplate restTemplate;

    public OrderController(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }
@PostMapping
    public String placeOrder(){
    // 1. Call Payment Service
    String paymentResponse =
            restTemplate.postForObject(
                    "http://localhost:8082/payments",
                    null,
                    String.class
            );

    // 2. Call Inventory Service
    String inventoryResponse =
            restTemplate.postForObject(
                    "http://localhost:8083/inventory/reserve",
                    null,
                    String.class
            );

    // 3. Return final response
    return "Order placed. " +
            "Payment: " + paymentResponse +
            ", Inventory: " + inventoryResponse;
    }
}
