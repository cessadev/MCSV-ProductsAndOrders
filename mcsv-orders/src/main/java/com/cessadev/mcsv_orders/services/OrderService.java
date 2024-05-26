package com.cessadev.mcsv_orders.services;

import com.cessadev.mcsv_orders.model.dtos.*;
import com.cessadev.mcsv_orders.model.entities.OrderEntity;
import com.cessadev.mcsv_orders.model.entities.OrderItemsEntity;
import com.cessadev.mcsv_orders.repositories.OrderRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public void createOrder(OrderRequestDTO orderRequestDTO) {

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("inventoryService");

        Mono<BaseResponse> resultMono = this.webClientBuilder.build()
                .post()
                .uri("lb://mcsv-inventory/api/inventory/in-stock")
                .bodyValue(orderRequestDTO.getOrderItems())
                .retrieve()
                .bodyToMono(BaseResponse.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(throwable -> {
                    log.error("Error occurred while calling inventory service", throwable);
                    return Mono.just(new BaseResponse(new String[]{"Fallback response: Inventory service is unavailable"}));
                });

        resultMono.subscribe(result -> {
            if (!result.hasErrors()) {
                OrderEntity order = new OrderEntity();
                order.setOrderNumber(UUID.randomUUID().toString());
                order.setOrderItems(orderRequestDTO.getOrderItems().stream()
                        .map(orderItemsRequestDTO -> mapToOrderItemsEntity(orderItemsRequestDTO, order))
                        .toList());
                this.orderRepository.save(order);
            } else {
                throw new IllegalArgumentException("Some of the products are not in stock");
            }
        });
    }

    public List<OrderResponseDTO> getAllOrders() {
        List<OrderEntity> orders = this.orderRepository.findAll();
        return orders.stream().map(this::mapToOrderResponseDTO).toList();
    }

    private OrderItemsEntity mapToOrderItemsEntity(OrderItemsRequestDTO orderItemsRequestDTO, OrderEntity orderEntity) {
        return OrderItemsEntity.builder()
                .id(orderItemsRequestDTO.getId())
                .sku(orderItemsRequestDTO.getSku())
                .price(orderItemsRequestDTO.getPrice())
                .quantity(orderItemsRequestDTO.getQuantity())
                .order(orderEntity)
                .build();
    }

    private OrderResponseDTO mapToOrderResponseDTO(OrderEntity orderEntity) {
        return new OrderResponseDTO(
                orderEntity.getId(),
                orderEntity.getOrderNumber(),
                orderEntity.getOrderItems().stream().map(this::mapToOrderItemsResponseDTO).toList());
    }

    private OrderItemsResponseDTO mapToOrderItemsResponseDTO(OrderItemsEntity orderItemsEntity) {
        return new OrderItemsResponseDTO(
                orderItemsEntity.getId(),
                orderItemsEntity.getSku(),
                orderItemsEntity.getPrice(),
                orderItemsEntity.getQuantity());
    }
}
