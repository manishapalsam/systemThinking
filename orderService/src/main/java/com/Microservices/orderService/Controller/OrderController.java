package com.Microservices.orderService.Controller;


import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log =
            LoggerFactory.getLogger(OrderController.class);

    private final RestTemplate restTemplate;

    public OrderController(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }
@PostMapping
    public String placeOrder(){
    // 1. Call Payment Service

    log.info("STEP 1: Order request received");

    log.info("STEP 2: Calling Payment Service");
    String paymentResponse =
            restTemplate.postForObject(
                    "http://localhost:8082/payments",
                    null,
                    String.class
            );



    log.info("STEP 3: Payment response received");

    log.info("STEP 4: Calling Inventory Service");

    // 2. Call Inventory Service
    String inventoryResponse =
            restTemplate.postForObject(
                    "http://localhost:8083/inventory/reserve",
                    null,
                    String.class
            );
    log.info("STEP 5: Inventory response received");

    // 3. Return final response
    return "Order placed. " +
            "Payment: " + paymentResponse +
            ", Inventory: " + inventoryResponse;
    }
}
