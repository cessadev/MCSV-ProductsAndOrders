package com.cessadev.mcsv_inventory.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemsRequestDTO {

    private Long id;
    private String sku;
    private BigDecimal price;
    private Long quantity;
}
