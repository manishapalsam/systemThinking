package com.Microservices.orderService.Repository;


import com.Microservices.orderService.Entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//manages refunds table
public interface RefundRepository extends JpaRepository<Refund, String> {

    List<Refund> findByOrderId(String orderId);

}
