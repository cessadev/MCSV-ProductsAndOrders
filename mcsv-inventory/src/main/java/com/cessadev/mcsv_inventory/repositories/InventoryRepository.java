package com.cessadev.mcsv_inventory.repositories;

import com.cessadev.mcsv_inventory.model.entities.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {

    Optional<InventoryEntity> isInStock(String sku);

    List<InventoryEntity> findBySkuIn(List<String> skus);
}
