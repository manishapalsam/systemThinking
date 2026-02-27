package com.Microservices.orderService.Repository;

import com.Microservices.orderService.Entity.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//manages orders table
@Repository
public interface OrderRepository  extends JpaRepository<Order, String> {


   // static Optional<Order> findById(String id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o fROM Order o WHERE o.id = :orderId")//internally SELECT * FROM orders WHERE id = ? FOR UPDATE  That FOR UPDATE is the lock.
    Optional<Order> findByIdForUpdate(@Param("orderId") String orderId);

    Optional<Order> findByCustomerId(String customerId);

Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);


}
