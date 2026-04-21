package org.systemthinking.payment_service2.service;

import org.systemthinking.payment_service2.Entity.Payment;
import org.systemthinking.payment_service2.dto.PaymentRequest;
import org.systemthinking.payment_service2.dto.PaymentResponse;
import org.systemthinking.payment_service2.exception.PaymentServiceException;
import org.systemthinking.payment_service2.repository.PaymentRepository;
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

       Thread.sleep(1000);//intentionall delay
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

