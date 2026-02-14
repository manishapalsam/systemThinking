package com.Microservices.orderService.Controller;


import com.Microservices.orderService.Entity.Order;
import com.Microservices.orderService.Service.OrderService;
import com.Microservices.orderService.dto.OrderRequest;
import com.Microservices.orderService.dto.OrderResponse;
import com.Microservices.orderService.dto.OrderSummaryResponse;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.support.PageableUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

//    private static final Logger log =
//            LoggerFactory.getLogger(OrderController.class);

    // private final RestTemplate restTemplate;

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        // this.restTemplate = restTemplate;
        this.orderService = orderService;

    }


    @PostMapping

    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {

        // @RequestBody → converts incoming JSON into OrderRequest object
        // @Valid → automatically checks validations (like quantity > 0)

        // Controller delegates business logic to service layer
        // WHY: Controller should be thin (RULE: Separation of concerns)

        OrderResponse orderResponse = orderService.createOrder(orderRequest);


        // Location header tells client WHERE the newly created resource exists
        // Example: /api/v1/orders/ORD-10001
        // RULE: Correct HTTP Semantics
        URI location = URI.create("/api/v1/orders" + orderResponse.getOrderId());
        //return new ResponseEntity<>(orderResponse, HttpStatus.CREATED);
        return ResponseEntity.created(location).body(orderResponse);// sets 201 + Location header
    }


//    Rule #2 – Resource-Oriented Design
///orders/{orderId} is a resource

//
//    Rule #3 – Correct HTTP Semantics
//    GET → read
//200 → found
//404 → not found
//
//    Rule #1 – API is a Contract
//    Clients depend on predictable responses


    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable("orderId") String orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    //Searh
    @GetMapping
    public ResponseEntity<Page<OrderSummaryResponse>> searchOrders(
            @RequestParam(name = "status", required = false) String status, Pageable pageable
    ) {
        Page<OrderSummaryResponse> result = orderService.searchOrders(status, pageable);
        return ResponseEntity.ok(result);
    }


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



