package com.cessadev.mcsv_inventory.controllers;

import com.cessadev.mcsv_inventory.model.dtos.BaseResponse;
import com.cessadev.mcsv_inventory.model.dtos.OrderItemsRequestDTO;
import com.cessadev.mcsv_inventory.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/find-by-sku/{sku}")
    @ResponseStatus(HttpStatus.OK)
    public boolean isInStock(@PathVariable String sku) {
        return inventoryService.isInStock(sku);
    }

    @PostMapping("in-stock")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse areInStock(@RequestBody List<OrderItemsRequestDTO> orderItemsRequestDTOS) {
        return inventoryService.areInStock(orderItemsRequestDTOS);
    }
}