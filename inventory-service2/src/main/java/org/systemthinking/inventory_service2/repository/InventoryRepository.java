package org.systemthinking.inventory_service2.repository;

import org.systemthinking.inventory_service2.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, String> {
        List<Inventory> findByProductIdIn(List<String> productIds);

        Optional<Inventory> findByProductId(String productId);

}

