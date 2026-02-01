package com.Microservices.orderService.Client;


import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;

@Component
public class PaymentClient {
    private static final Logger log =
            LoggerFactory.getLogger(PaymentClient.class);

    private final RestTemplate restTemplate;

    public  PaymentClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public void  processPayment(){
        log.info("calling payment service");

        restTemplate.postForObject(
                "http://localhost:8082/payments",
                null,
                String.class
        );

        log.info("Payment Service responded");
    }
}
