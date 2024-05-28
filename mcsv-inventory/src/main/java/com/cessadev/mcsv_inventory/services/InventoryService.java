package com.cessadev.mcsv_inventory.services;

import com.cessadev.mcsv_inventory.model.dtos.BaseResponse;
import com.cessadev.mcsv_inventory.model.dtos.OrderItemsRequestDTO;
import com.cessadev.mcsv_inventory.model.entities.InventoryEntity;
import com.cessadev.mcsv_inventory.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public ResponseEntity<Boolean> isInStock(String sku) {
        try {
            Optional<InventoryEntity> inventory = inventoryRepository.findBySku(sku);
            boolean inStock = inventory.filter(inventoryEntity -> inventoryEntity.getQuantity() > 0).isPresent();
            return ResponseEntity.ok(inStock);

        } catch (DataAccessException e) {
            log.error("DataAccessException occurred while checking stock for SKU: {}", sku, e);
            return ResponseEntity.status(500).body(false);
        } catch (RuntimeException e) {
            log.error("Unexpected error occurred while checking stock for SKU: {}", sku, e);
            return ResponseEntity.status(500).body(false);
        }
    }

    public ResponseEntity<BaseResponse> areInStock(List<OrderItemsRequestDTO> orderItemsRequestDTOS) {
        ArrayList<String> errorList = new ArrayList<>();
        try {
            List<String> skus = orderItemsRequestDTOS.stream().map(OrderItemsRequestDTO::getSku).toList();
            List<InventoryEntity> inventories = inventoryRepository.findBySkuIn(skus);

            orderItemsRequestDTOS.forEach(orderItemRequestDTO -> {
                Optional<InventoryEntity> inventory = inventories.stream()
                        .filter(value -> value.getSku().equals(orderItemRequestDTO.getSku()))
                        .findFirst();
                if (inventory.isEmpty()) {
                    errorList.add("Product with sku " + orderItemRequestDTO.getSku() + " does not exist");
                } else if (inventory.get().getQuantity() < orderItemRequestDTO.getQuantity()) {
                    errorList.add("Product with sku " + orderItemRequestDTO.getSku() + " has insufficient quantity");
                }
            });

            if (!errorList.isEmpty()) {
                return ResponseEntity.badRequest().body(new BaseResponse(errorList.toArray(new String[0])));
            } else {
                return ResponseEntity.ok(new BaseResponse(null));
            }
        } catch (DataAccessException e) {
            log.error("DataAccessException occurred while checking stock", e);
            return ResponseEntity.status(500).body(new BaseResponse(new String[]{"Database error occurred while checking stock"}));
        } catch (RuntimeException e) {
            log.error("Unexpected error occurred while checking stock", e);
            return ResponseEntity.status(500).body(new BaseResponse(new String[]{"Unexpected error occurred while checking stock"}));
        }
    }

//        ArrayList<String> errorList = new ArrayList<>();
//        List<String> skus = orderItemsRequestDTOS.stream().map(OrderItemsRequestDTO::getSku).toList();
//        List<InventoryEntity> inventories = inventoryRepository.findBySkuIn(skus);
//
//        orderItemsRequestDTOS.forEach(orderItemRequestDTO -> {
//            Optional<InventoryEntity> inventory = inventories.stream().filter(value -> value.getSku().equals(orderItemRequestDTO.getSku())).findFirst();
//            if (inventory.isEmpty()) {
//                errorList.add("Product with sku " + orderItemRequestDTO.getSku() + " does not exists");
//            } else if (inventory.get().getQuantity() < orderItemRequestDTO.getQuantity()) {
//                errorList.add("Product with sku " + orderItemRequestDTO.getSku() + " has insufficient quantity");
//            }
//        });
//        return !errorList.isEmpty() ? new BaseResponse(errorList.toArray(new String[0])) : new BaseResponse(null);
//    }
}