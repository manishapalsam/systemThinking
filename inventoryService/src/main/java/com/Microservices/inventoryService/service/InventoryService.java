package com.Microservices.inventoryService.service;

import com.Microservices.inventoryService.dto.InventoryRequest;
import com.Microservices.inventoryService.entity.Inventory;
import com.Microservices.inventoryService.exception.InventoryServiceException;
import com.Microservices.inventoryService.exception.OutOfStockException;
import com.Microservices.inventoryService.exception.ProductNotFoundException;
import com.Microservices.inventoryService.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;




//actual db call
@Service
public class InventoryService {

    private static  final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionService transactionService;



     // public InventoryService(){}

    public InventoryService(InventoryRepository inventoryRepository, InventoryTransactionService transactionService) {
        this.inventoryRepository = inventoryRepository;
        this.transactionService = transactionService;
    }




    public void reserveInventory(InventoryRequest inventoryRequest) {

        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                attempt++;
                logger.info("Reserve attempt {} for {}", attempt, inventoryRequest);

                transactionService.reserveInventoryInternal(inventoryRequest);

                return; // success

            } catch (ObjectOptimisticLockingFailureException |
                     org.hibernate.StaleObjectStateException ex) {

                if (attempt >= maxRetries) {
                    throw new InventoryServiceException("Inventory busy, please retry order");
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }




    //inventory service
    public Integer getAvailableStock(String productId){
        try{
            Inventory inventory = inventoryRepository.findById(productId)
                    .orElseThrow(()-> new InventoryServiceException("Product not found: " + productId));
            return inventory.getAvailableStock();
        }
        catch(Exception e){
            logger.error("Failed to get stock for product {}: {}", productId, e.getMessage(), e);
            throw new InventoryServiceException("Failed to get stock: " + e.getMessage(), e);
        }
    }
}
