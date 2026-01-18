package com.Microservices.orderService.Service;

import com.Microservices.orderService.Client.InventoryClient;
import com.Microservices.orderService.Client.PaymentClient;
import com.Microservices.orderService.Entity.Order;
import com.Microservices.orderService.Repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final InventoryClient inventoryClient;
    public OrderService(OrderRepository orderRepository,
                        PaymentClient paymentClient,
                        InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.paymentClient = paymentClient;
        this.inventoryClient = inventoryClient;
    }

    public  Order createOrder(String product, int quantity, String status){
        paymentClient.processPayment();
        inventoryClient.reserveInventory();
        Order order = new Order(product,quantity,status);


        return  orderRepository.save(order);
    }

}
