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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryTransactionService {



    private final InventoryRepository inventoryRepository;

    public  InventoryTransactionService(InventoryRepository inventoryRepository){
        this.inventoryRepository = inventoryRepository;
    }
    private static final Logger logger = LoggerFactory.getLogger(InventoryTransactionService.class);

    @Transactional
    public void reserveInventoryInternal(InventoryRequest inventoryRequest) {

        List<String> productIds = inventoryRequest.getItems().stream()
                .map(InventoryRequest.InventoryItemRequest::getProductId)
                .collect(Collectors.toList());

        List<Inventory> inventories =
                inventoryRepository.findByProductIdIn(productIds);

        if (inventories.size() != productIds.size()) {
            throw new ProductNotFoundException("Multiple products missing");
        }

        for (InventoryRequest.InventoryItemRequest item : inventoryRequest.getItems()) {

            Inventory inventory = inventories.stream()
                    .filter(inv -> inv.getProductId().equals(item.getProductId()))
                    .findFirst()
                    .orElseThrow(() ->
                            new InventoryServiceException("Product not found: "
                                    + item.getProductId()));

            if (inventory.getAvailableStock() < item.getQuantity()) {
               // logger.info();
                throw new OutOfStockException(item.getProductId());
            }

            inventory.setReservedQuantity(
                    inventory.getReservedQuantity() + item.getQuantity()
            );

            inventoryRepository.save(inventory);
        }
    }
}
