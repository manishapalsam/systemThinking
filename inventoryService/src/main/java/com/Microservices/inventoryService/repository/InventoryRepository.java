package com.Microservices.inventoryService.repository;

import com.Microservices.inventoryService.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {
    List<Inventory> findByProductIdIn(List<String> productIds);
}
