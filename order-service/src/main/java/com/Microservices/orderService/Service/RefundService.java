package com.Microservices.orderService.Service;

import com.Microservices.orderService.Entity.Order;
import com.Microservices.orderService.Entity.Refund;
import com.Microservices.orderService.Repository.OrderRepository;
import com.Microservices.orderService.Repository.RefundRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
public class RefundService {

    private final OrderRepository orderRepository;
    private final RefundRepository refundRepository;

    public RefundService(OrderRepository orderRepository, RefundRepository refundRepository) {
        this.orderRepository = orderRepository;
        this.refundRepository = refundRepository;
    }



    //vulnerable logic
    @Transactional //Transaction does NOT solve race condition
    public void refundOrder(String orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<Refund> existingRefunds = refundRepository.findByOrderId(orderId);

        if(!existingRefunds.isEmpty()){
            throw new RuntimeException("Refund already processed");
        }

        //  Artificial delay to widen race window
        try{
            Thread.sleep(10000);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }


        //create refund
        Refund refund = new Refund(
                UUID.randomUUID().toString(),
                order,
                Refund.RefundStatus.SUCCESS,
                LocalDateTime.now()
        );

        refundRepository.save(refund);
    }
}
