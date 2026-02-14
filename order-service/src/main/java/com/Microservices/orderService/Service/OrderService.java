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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Transactional
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final RestTemplate restTemplate;

    //    private final PaymentClient paymentClient;
//    private final InventoryClient inventoryClient;
    public OrderService(OrderRepository orderRepository,
                        RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public OrderResponse createOrder(OrderRequest orderRequest) {
//        paymentClient.processPayment();
//        inventoryClient.reserveInventory();

        try {
            logger.info("Creating order for customer: {}", orderRequest.getCustomerId());

            // Generate order ID
            String orderId = "ORD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            //reserve inventory
            reserveInventory(orderRequest);

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
            //save order
            orderRepository.save(order);
            logger.info("Order created successfully with id: {}", orderId);

            // Return response
            return mapToOrderResponse(order);
        } catch (Exception e) {
            logger.error("Failed to create order for customer {}: {}", orderRequest.getCustomerId(), e.getMessage(), e);
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
        String inventoryServiceUrl = "http://localhost:8082/api/v1/inventory/reserve";


        try {
            logger.info("Reserving inventory for order request");
            List<InventoryItemRequest> inventoryItems = orderRequest.getItems().stream()
                    .map(item -> new InventoryItemRequest(item.getProductId(), item.getQuantity()))
                    .collect(Collectors.toList());
            InventoryRequest inventoryRequest = new InventoryRequest(inventoryItems);
            restTemplate.postForEntity(inventoryServiceUrl, inventoryRequest, Void.class);
            logger.debug("Inventory reserved successfully");
        } catch (HttpClientErrorException.Conflict ex) {
            // Inventory Business Conflict → Order Business Conflict


            DownstreamApiError error = parseError(ex);
            throw new BusinessConflictException(
                    error.getDetails(),
                    error.getTitle());
        } catch (Exception ex) {
            logger.error("Failed to reserve inventory: {}", ex.getMessage(), ex);
            //  throw new OrderServiceException("Failed to reserve inventory: " + e.getMessage(), e);
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
            restTemplate.postForEntity(paymentServiceUrl, paymentRequest, Void.class);
            logger.debug("Payment processed successfully for order: {}", orderId);
        } catch (Exception e) {
            logger.error("Failed to process payment for order {}: {}", orderId, e.getMessage(), e);
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


    private DownstreamApiError  parseError(HttpClientErrorException ex) {

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
}


