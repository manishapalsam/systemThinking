package com.Microservices.paymentService.service;


import com.Microservices.paymentService.Payment;
import com.Microservices.paymentService.dto.PaymentRequest;
import com.Microservices.paymentService.dto.PaymentResponse;
import com.Microservices.paymentService.exception.PaymentServiceException;
import com.Microservices.paymentService.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;

    }
    public PaymentResponse processPayment(PaymentRequest paymentRequest){
       try{
           logger.info("Processing payment for order: {}, amount: {}", paymentRequest.getOrderId(), paymentRequest.getAmount());
       String transactionId = "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

       Thread.sleep(8000);
       //create payment record
           Payment payment = new Payment();
           payment.setId(transactionId);
           payment.setOrderId(paymentRequest.getOrderId());
           payment.setAmount(paymentRequest.getAmount());
           payment.setMethod(paymentRequest.getMethod());
           payment.setStatus(Payment.PaymentStatus.COMPLETED);
           payment.setProcessedAt(LocalDateTime.now());


           //save payment
           paymentRepository.save(payment);
           logger.info("Payment processed successfully for order: {}", paymentRequest.getOrderId());

           // Return response
           return new PaymentResponse(
                   payment.getId(),
                   payment.getOrderId(),
                   payment.getAmount(),
                   payment.getMethod(),
                   payment.getStatus().toString(),
                   payment.getProcessedAt()
           );

       }catch (Exception e){
           logger.error("Failed to process payment for order {}: {}", paymentRequest.getOrderId(), e.getMessage(), e);
           throw new PaymentServiceException("Failed to process payment: " + e.getMessage(), e);
       }
    }
}
