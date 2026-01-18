package com.Microservices.orderService.Client;


import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class InventoryClient {

    private static final Logger log =
            LoggerFactory.getLogger(InventoryClient.class);

    private final RestTemplate restTemplate;

    public InventoryClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void reserveInventory() {
        log.info("Calling Inventory Service");

        restTemplate.postForObject(
                "http://localhost:8083/inventory/reserve",
                null,
                String.class
        );

        log.info("Inventory Service responded");
    }
}
