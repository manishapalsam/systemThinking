package com.Microservices.orderService.Service;




import com.Microservices.orderService.Client.InventoryClient;
import com.Microservices.orderService.Client.PaymentClient;
import com.Microservices.orderService.Entity.Order;
import com.Microservices.orderService.Entity.OrderItem;
import com.Microservices.orderService.Repository.OrderRepository;
import com.Microservices.orderService.dto.*;
import com.Microservices.orderService.exception.OrderServiceException;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
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

    public  OrderResponse createOrder(OrderRequest orderRequest){
//        paymentClient.processPayment();
//        inventoryClient.reserveInventory();

        try{
            logger.info("Creating order for customer: {}", orderRequest.getCustomerId());

            // Generate order ID
            String orderId = "ORD" + UUID.randomUUID().toString().substring(0,8).toUpperCase();

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
        catch(Exception e){
           logger.error("Failed to create order for customer {}: {}", orderRequest.getCustomerId(), e.getMessage(), e);
           throw new OrderServiceException("Failed to create order: " + e.getMessage(), e);
        }

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
        }catch (Exception e) {
            logger.error("Failed to reserve inventory: {}", e.getMessage(), e);
            throw new OrderServiceException("Failed to reserve inventory: " + e.getMessage(), e);
        }
    }
    private void processPayment(String orderId, Double amount, String paymentMethod){
        String paymentServiceUrl = "http://localhost:8081/api/v1/payments";


        try{
            logger.debug("Processing payment for order: {}, amount: {}", orderId, amount);
            PaymentRequest paymentRequest = new PaymentRequest(orderId,amount,paymentMethod);
           restTemplate.postForEntity(paymentServiceUrl,paymentRequest, Void.class);
            logger.debug("Payment processed successfully for order: {}", orderId);
        }catch(Exception e){
            logger.error("Failed to process payment for order {}: {}", orderId, e.getMessage(), e);
            throw new OrderServiceException("Failed to process payment: " + e.getMessage(), e);
        }
    }

    private Double calculateTotalAmount(OrderRequest orderRequest){
        return orderRequest.getItems().stream()
                .mapToDouble(item -> item.getQuantity() * 100.0)
                .sum();
    }
}


