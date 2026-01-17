package com.Microservices.inventoryService.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/inventory")
public class InventoryController {
    @PostMapping("/reserve")
    public String reserveInventory() {
        return "Inventory reserved";
    }
}
