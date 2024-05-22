package com.cessadev.mcsv_orders.model.dtos;

import java.math.BigDecimal;

public record OrderItemsResponseDTO(Long id, String sku, BigDecimal price, Long quantity) {
}
