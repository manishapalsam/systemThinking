
package com.Microservices.orderService.Service;





import com.Microservices.orderService.Client.InventoryClient;
import com.Microservices.orderService.Client.PaymentClient;
import com.Microservices.orderService.Entity.Order;
import com.Microservices.orderService.Entity.OrderItem;
import com.Microservices.orderService.Repository.OrderRepository;
import com.Microservices.orderService.dto.*;
import com.Microservices.orderService.exception.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Transactional
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final RestTemplate restTemplate;
    private final PaymentClient paymentClient;
    private final InventoryClient inventoryClient;

    //    private final PaymentClient paymentClient;
//    private final InventoryClient inventoryClient;
    public OrderService(OrderRepository orderRepository,
                        RestTemplate restTemplate,
                        PaymentClient paymentClient,
                        InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
        this.paymentClient = paymentClient;
        this.inventoryClient = inventoryClient;
    }

    public OrderResponse createOrder(OrderRequest orderRequest) {
//        paymentClient.processPayment();
//        inventoryClient.reserveInventory();
        boolean inventoryReserved = false;

        try {
            logger.info("Creating order for customer: {}", orderRequest.getCustomerId());

            // Generate order ID
            String orderId = "ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            //reserve inventory
            reserveInventory(orderRequest);
            inventoryReserved = true;
            //process payment
            processPayment(orderId, calculateTotalAmount(orderRequest), orderRequest.getPaymentMethod());


            //create order
            Order order = new Order();
            order.setId(orderId);
            order.setCustomerId(orderRequest.getCustomerId());
            order.setStatus(Order.OrderStatus.CONFIRMED);
            order.setTotalAmount(calculateTotalAmount(orderRequest));
            order.setCreatedAt(LocalDateTime.now());

            //create order item
            List<OrderItem> orderIems = orderRequest.getItems().stream()
                    .map(item -> new OrderItem(
                            UUID.randomUUID().toString(),
                            order,
                            item.getProductId(),
                            item.getQuantity(),
                            100.0
                    ))
                    .collect(Collectors.toList());
            order.setItems(orderIems);

            System.out.println(orderIems.stream()
                    .map(o-> o.getQuantity())

                    .count());


            //map->filter
           List<Integer> prices = orderIems.stream()
                    .map(o -> o.getQuantity())
                            .filter(o -> o > 1)  // map → filter → sorted → collect filter reduces data early
                                    .sorted()
                                            .collect(Collectors.toList());
            System.out.println(prices);


            //filter → map(filter reduces data early)
            orderIems.stream()
                    .map(OrderItem::getQuantity)
                    .sorted(Comparator.reverseOrder())
                    .forEach(System.out::println);

            //save order
            orderRepository.save(order);
            logger.info("Order created successfully with id: {}", orderId);

            // Return response
            return mapToOrderResponse(order);
        } catch (Exception e) {
            logger.error("Failed to create order for customer {}: {}", orderRequest.getCustomerId(), e.getMessage(), e);
            if (inventoryReserved) {
                try {
                    InventoryRequest inventoryRequest = buildInventoryRequest(orderRequest);
                    inventoryClient.releaseInventory(inventoryRequest);
                    logger.info("Inventory rolled back successfully");
                } catch (Exception ex) {
                    logger.error("Failed to rollback inventory", ex);
                }
            }


            throw new OrderServiceException("Failed to create order: " + e.getMessage(), e);
        }

    }


    public OrderResponse getOrderById(String orderId) {
        logger.info("Fetching order with id: {}", orderId);
        Order order = orderRepository.findById(orderId)
                //.orElseThrow(()-> new OrderServiceException("Order not found: " + orderId));
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        return mapToOrderResponse(order);
    }


    public Page<OrderSummaryResponse> searchOrders(String status, Pageable pageable) {

        Page<Order> orders;

        if (status != null) {
            orders = orderRepository.findByStatus(Order.OrderStatus.valueOf(status), pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }

        //Converts Entity → DTO
        return orders.map(
                order -> new OrderSummaryResponse(
                        order.getId(),
                        order.getStatus().toString()
                )
        );
    }


    private void reserveInventory(OrderRequest orderRequest) {
       // String inventoryServiceUrl = "http://localhost:8082/api/v1/inventory/reserve";


        try {
            logger.info("Reserving inventory for order request");
            List<InventoryItemRequest> inventoryItems = orderRequest.getItems().stream()
                    .map(item -> new InventoryItemRequest(item.getProductId(), item.getQuantity()))
                    .collect(Collectors.toList());
            InventoryRequest inventoryRequest = new InventoryRequest(inventoryItems);
            inventoryClient.reserveInventory(inventoryRequest);
            logger.debug("Inventory reserved successfully");
        } catch (WebClientResponseException.Conflict ex) {
            // Inventory Business Conflict → Order Business Conflict

            System.out.println(ex.getResponseBodyAsString());
            DownstreamApiError error = parseError(ex);
            throw new BusinessConflictException(
                    error.getTitle(),     // errorCode
                    error.getDetails()) ; // message
        } catch (Exception ex) {
            // 🔹 Check if it's circuit breaker / fallback
            if (ex.getMessage().contains("Inventory failed")) {
                throw new DownstreamServiceException(
                        "Inventory service is down (circuit open)",
                        ex
                );
            }

            logger.error("Failed to reserve inventory: {}", ex.getMessage(), ex);

            throw new DownstreamServiceException(
                    "Inventory service unavailable",
                    ex
            );
        }

    }

    private void processPayment(String orderId, Double amount, String paymentMethod) {
        String paymentServiceUrl = "http://localhost:8081/api/v1/payments";


        try {
            logger.debug("Processing payment for order: {}, amount: {}", orderId, amount);
            PaymentRequest paymentRequest = new PaymentRequest(orderId, amount, paymentMethod);
            paymentClient.processPayment(paymentRequest);
            logger.debug("Payment processed successfully for order: {}", orderId);
        } catch (Exception e) {
            logger.error("Failed to process payment for order {}: {}", orderId, e.getMessage(), e);


            logger.error("Order creation failed: {}", e.getMessage(), e);


            //throw new OrderServiceException("Failed to process payment: " + e.getMessage(), e);

            throw new DownstreamServiceException(
                    "Payment service unavailable",
                    e
            );
        }
    }

    private Double calculateTotalAmount(OrderRequest orderRequest) {
        return orderRequest.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * 100.0)
                .sum();
    }


    //reusable
    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus().toString(),
                order.getTotalAmount(),
                order.getItems().stream()
                        .map(item -> new OrderResponse.OrderItemResponse(
                                item.getProductId(),
                                item.getQuantity(),
                                item.getPrice()
                        ))
                        .collect(Collectors.toList()),
                order.getCreatedAt()
        );
    }

//We use ObjectMapper to convert the raw JSON error response from another service into a Java object so we can read and use its fields (like title and details).
    private DownstreamApiError  parseError(WebClientResponseException ex) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(
                    ex.getResponseBodyAsString(),
                    DownstreamApiError.class
            );
        } catch (Exception e) {
            DownstreamApiError fallback = new DownstreamApiError();
            fallback.setTitle("INVENTORY_ERROR");
            fallback.setDetails("Inventory service error");
            return fallback;
        }
    }



    public void  sortingPrice(Order orders){

    }


    private InventoryRequest buildInventoryRequest(OrderRequest orderRequest) {
        List<InventoryItemRequest> items = orderRequest.getItems().stream()
                .map(i -> new InventoryItemRequest(i.getProductId(), i.getQuantity()))
                .collect(Collectors.toList());

        return new InventoryRequest(items);
    }
}

//14-2-26

// If you don’t separate:
// You tightly couple Order Service’s internal error model to Inventory’s error model.
//
//        If Inventory changes tomorrow → your Order Service may break.
//
//        ✅ Best Practice:
//
//        Use DownstreamApiError for deserializing external response.
//
//        Convert it into your own ApiError before returning.
//
//        This keeps services loosely coupled (microservice principle).
