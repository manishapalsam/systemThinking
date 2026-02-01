package com.Microservices.inventoryService.Controller;

import com.Microservices.inventoryService.dto.InventoryRequest;
import com.Microservices.inventoryService.service.InventoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    private static final Logger log =
            LoggerFactory.getLogger(InventoryController.class);

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveInventory(@Valid @RequestBody InventoryRequest inventoryRequest) {
        inventoryService.reserveInventory(inventoryRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Integer> getAvailableStock(@PathVariable String productId){
        Integer stock = inventoryService.getAvailableStock(productId);
        return ResponseEntity.ok(stock);
    }

    }

