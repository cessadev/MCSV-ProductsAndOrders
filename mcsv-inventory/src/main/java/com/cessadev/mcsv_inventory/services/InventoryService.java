package com.cessadev.mcsv_inventory.services;

import com.cessadev.mcsv_inventory.model.dtos.BaseResponse;
import com.cessadev.mcsv_inventory.model.dtos.OrderItemsRequestDTO;
import com.cessadev.mcsv_inventory.model.entities.InventoryEntity;
import com.cessadev.mcsv_inventory.repositories.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public boolean isInStock(String sku) {
        Optional<InventoryEntity> inventory = inventoryRepository.isInStock(sku);
        return inventory.filter(inventoryEntity -> inventoryEntity.getQuantity() > 0).isPresent();
    }

    public BaseResponse areInStock(List<OrderItemsRequestDTO> orderItemsRequestDTOS) {
        ArrayList<String> errorList = new ArrayList<>();
        List<String> skus = orderItemsRequestDTOS.stream().map(OrderItemsRequestDTO::getSku).toList();
        List<InventoryEntity> inventories = inventoryRepository.findBySkuIn(skus);

        orderItemsRequestDTOS.forEach(orderItemRequestDTO -> {
            Optional<InventoryEntity> inventory = inventories.stream().filter(value -> value.getSku().equals(orderItemRequestDTO.getSku())).findFirst();
            if (inventory.isEmpty()) {
                errorList.add("Product with sku " + orderItemRequestDTO.getSku() + " does not exists");
            } else if (inventory.get().getQuantity() < orderItemRequestDTO.getQuantity()) {
                errorList.add("Product with sku " + orderItemRequestDTO.getSku() + " has insufficient quantity");
            }
        });
        return errorList.size() > 0 ? new BaseResponse(errorList.toArray(new String[0])) : new BaseResponse(null);
    }
}
