package com.Microservices.orderService.Client;


import com.Microservices.orderService.dto.InventoryRequest;
import org.slf4j.LoggerFactory;
import com.Microservices.orderService.dto.PaymentRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import org.slf4j.Logger;

@Component
public class PaymentClient {
    private static final Logger log =
            LoggerFactory.getLogger(PaymentClient.class);

    private final WebClient paymentWebClient;

    public  PaymentClient(@Qualifier("paymentWebClient") WebClient paymentWebClient){
        this.paymentWebClient = paymentWebClient;
    }


    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(
            name = "payment",
            fallbackMethod = "paymentFallback"
    )
    public void  processPayment(PaymentRequest paymentRequest){
        log.info("calling payment service");

        paymentWebClient
                .post()
                .uri("/api/v1/payments")
                .bodyValue(paymentRequest)
                .retrieve()
                .toBodilessEntity()
                .block();

        log.info("Payment Service responded");
    }

    public  void paymentFallback(PaymentRequest request, Throwable ex){
        throw new RuntimeException("Payment failed, stopping order"); //degradded mode ie payment pending
    }

}
