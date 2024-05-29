package com.cessadev.mcsv_orders.mapper;

import com.cessadev.mcsv_orders.model.dtos.OrderItemsRequestDTO;
import com.cessadev.mcsv_orders.model.dtos.OrderItemsResponseDTO;
import com.cessadev.mcsv_orders.model.dtos.OrderResponseDTO;
import com.cessadev.mcsv_orders.model.entities.OrderEntity;
import com.cessadev.mcsv_orders.model.entities.OrderItemsEntity;

public class OrderMapper {

    public static OrderItemsEntity mapToOrderItemsEntity(OrderItemsRequestDTO orderItemsRequestDTO, OrderEntity orderEntity) {
        return OrderItemsEntity.builder()
                .id(orderItemsRequestDTO.getId())
                .sku(orderItemsRequestDTO.getSku())
                .price(orderItemsRequestDTO.getPrice())
                .quantity(orderItemsRequestDTO.getQuantity())
                .order(orderEntity)
                .build();
    }

    public OrderResponseDTO mapToOrderResponseDTO(OrderEntity orderEntity) {
        return new OrderResponseDTO(
                orderEntity.getId(),
                orderEntity.getOrderNumber(),
                orderEntity.getOrderItems().stream().map(this::mapToOrderItemsResponseDTO).toList());
    }

    public OrderItemsResponseDTO mapToOrderItemsResponseDTO(OrderItemsEntity orderItemsEntity) {
        return new OrderItemsResponseDTO(
                orderItemsEntity.getId(),
                orderItemsEntity.getSku(),
                orderItemsEntity.getPrice(),
                orderItemsEntity.getQuantity());
    }
}
