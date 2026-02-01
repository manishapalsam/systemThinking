package com.Microservices.inventoryService.config;

import com.Microservices.inventoryService.entity.Inventory;
import com.Microservices.inventoryService.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception{
        // Initialize sample inventory data
        inventoryRepository.save(new Inventory("P1", 400, 0));
        inventoryRepository.save(new Inventory("P2", 400, 0));
        inventoryRepository.save(new Inventory("P3", 400, 0));
        inventoryRepository.save(new Inventory("P4", 400, 0));
        inventoryRepository.save(new Inventory("P5", 400, 0));

        System.out.println("Sample inventory data initialized");

    }
}
