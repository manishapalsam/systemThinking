package com.Microservices.orderService.Repository;

import com.Microservices.orderService.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository  extends JpaRepository<Order, Long> {



}
