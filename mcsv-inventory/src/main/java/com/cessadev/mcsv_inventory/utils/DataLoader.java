package com.cessadev.mcsv_inventory.utils;

import com.cessadev.mcsv_inventory.model.entities.InventoryEntity;
import com.cessadev.mcsv_inventory.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Loading data...");
        if (inventoryRepository.findAll().isEmpty()) {
            inventoryRepository.saveAll(
                    List.of(
                            InventoryEntity.builder().sku("000001").quantity(10L).build(),
                            InventoryEntity.builder().sku("000002").quantity(20L).build(),
                            InventoryEntity.builder().sku("000003").quantity(30L).build(),
                            InventoryEntity.builder().sku("000004").quantity(0L).build()
                    )
            );
        }
        log.info("Data loaded.");
    }
}
