package com.Microservices.orderService.Repository;

import com.Microservices.orderService.Entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//manages orders table
@Repository
public interface OrderRepository  extends JpaRepository<Order, String> {


   // static Optional<Order> findById(String id);
    Optional<Order> findByCustomerId(String customerId);

Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);


}
