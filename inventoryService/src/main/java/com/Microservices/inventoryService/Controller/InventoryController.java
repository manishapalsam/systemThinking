package com.Microservices.inventoryService.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private static final Logger log =
            LoggerFactory.getLogger(InventoryController.class);


    @PostMapping("/reserve")
    public String reserveInventory() throws InterruptedException {

        log.info("Inventory request received - simulating slowness");

        // Simulate slow dependency (10 seconds)
        Thread.sleep(500);

        log.info("Inventory processing completed");

        return "Inventory reserved";
    }
    }

