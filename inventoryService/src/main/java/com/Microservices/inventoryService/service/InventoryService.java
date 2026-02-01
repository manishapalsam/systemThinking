package com.Microservices.inventoryService.service;

import com.Microservices.inventoryService.dto.InventoryRequest;
import com.Microservices.inventoryService.entity.Inventory;
import com.Microservices.inventoryService.exception.InventoryServiceException;
import com.Microservices.inventoryService.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Transactional
@Service
public class InventoryService {

    private static  final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;

   // public InventoryService(){}

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }


public  void reserveInventory(InventoryRequest inventoryRequest){
        try{
            logger.info("Reserving inventory for request: {}", inventoryRequest);
            List<String> productIds = inventoryRequest.getItems().stream()
                    .map(InventoryRequest.InventoryItemRequest::getProductId)
                    .collect(Collectors.toList());


            List<Inventory> inventories = inventoryRepository.findByProductIdIn(productIds);
            // Check if all products exist
        if(inventories.size() != productIds.size()){
            logger.error("One or more products not found for ids: {}", productIds);
            throw new InventoryServiceException("One or more products not found");
        }

           // Check availability and reserve
        for(InventoryRequest.InventoryItemRequest item : inventoryRequest.getItems()){
            Inventory inventory = inventories.stream()
                    .filter(inv -> inv.getProductId().equals(item.getProductId()))
                    .findFirst()
                    .orElseThrow(() -> new InventoryServiceException("Product not found: " + item.getProductId()));
            if(inventory.getAvailableStock() < item.getQuantity()){
                logger.warn("Insufficient stock for product: {} , available: {}, requested: {}", item.getProductId(), inventory.getAvailableStock(), item.getQuantity());
                throw new InventoryServiceException("Insufficient stock for product: " + item.getProductId());
            }
            // Reserve the inventory
            inventory.setReservedQuantity(inventory.getReservedQuantity() + item.getQuantity());
            inventoryRepository.save(inventory);
            logger.info("Reserved {} units of product {}", item.getQuantity(), item.getProductId());
            }
        }
        catch (Exception e) {
            logger.error("Failed to reserve inventory: {}", e.getMessage(), e);
            throw new InventoryServiceException("Failed to reserve inventory: " + e.getMessage(), e);
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
