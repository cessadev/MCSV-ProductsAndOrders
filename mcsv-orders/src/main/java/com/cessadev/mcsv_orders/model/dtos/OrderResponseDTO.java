package com.cessadev.mcsv_orders.model.dtos;

import java.util.List;

public record OrderResponseDTO(Long id, String orderNumber, List<OrderItemsResponseDTO> orderItems) {
}
