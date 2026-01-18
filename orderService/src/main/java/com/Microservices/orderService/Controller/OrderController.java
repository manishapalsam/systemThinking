package com.Microservices.orderService.Controller;


import com.Microservices.orderService.Entity.Order;
import com.Microservices.orderService.Service.OrderService;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
@RestController
@RequestMapping("/orders")
public class OrderController {

    private static final Logger log =
            LoggerFactory.getLogger(OrderController.class);

   // private final RestTemplate restTemplate;

    private  final OrderService orderService;

    public OrderController( OrderService orderService){
       // this.restTemplate = restTemplate;
        this.orderService = orderService;

    }


    @PostMapping
    public Order createOrder(@RequestParam String product, @RequestParam int quantity){
        return orderService.createOrder(product, quantity, "CREATED");
    }
//
//
//    @PostMapping
//    public String placeOrder(){
//
//        try{
//            // 1. Call Payment Service
//
//            log.info("STEP 1: Order request received");
//
//            log.info("STEP 2: Calling Payment Service");
//            String paymentResponse =
//                    restTemplate.postForObject(
//                            "http://localhost:8082/payments",
//                            null,
//                            String.class
//                    );
//
//
//
//            log.info("STEP 3: Payment response received");
//
//            log.info("STEP 4: Calling Inventory Service");
//
//            // 2. Call Inventory Service
//            String inventoryResponse =
//                    restTemplate.postForObject(
//                            "http://localhost:8083/inventory/reserve",
//                            null,
//                            String.class
//                    );
//            log.info("STEP 5: Inventory response received");
//
//            // 3. Return final response
//            return "Order placed. " +
//                    "Payment: " + paymentResponse +
//                    ", Inventory: " + inventoryResponse;
//        }
//        catch (ResourceAccessException ex) {
//
//            log.error("Downstream service failure", ex);
//            return "Order could not be completed. Please try again later.";
//        }

    // }
}


