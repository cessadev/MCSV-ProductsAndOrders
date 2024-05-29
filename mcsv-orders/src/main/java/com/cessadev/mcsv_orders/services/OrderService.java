package com.cessadev.mcsv_orders.services;

import com.cessadev.mcsv_orders.mapper.OrderMapper;
import com.cessadev.mcsv_orders.model.dtos.*;
import com.cessadev.mcsv_orders.model.entities.OrderEntity;
import com.cessadev.mcsv_orders.repositories.OrderRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public Mono<ResponseEntity<String>> createOrder(OrderRequestDTO orderRequestDTO) {

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("inventoryService");

        Mono<BaseResponse> resultMono = this.webClientBuilder.build()
                .post()
                .uri("lb://mcsv-inventory/api/inventory/in-stock")
                .bodyValue(orderRequestDTO.getOrderItems())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    return clientResponse.bodyToMono(BaseResponse.class)
                            .flatMap(response -> Mono.error(new WebClientResponseException(
                                    clientResponse.statusCode().value(),
                                    "Client Error: " + Arrays.toString(response.errorMessage()),
                                                                clientResponse.headers().asHttpHeaders(),
                                                                null,
                                                                null)));
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    return clientResponse.bodyToMono(BaseResponse.class)
                            .flatMap(response -> Mono.error(new WebClientResponseException(
                                    clientResponse.statusCode().value(),
                                    "Server Error: " + Arrays.toString(response.errorMessage()),
                                                                clientResponse.headers().asHttpHeaders(),
                                                                null,
                                                                null)));
                })
                .bodyToMono(BaseResponse.class)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                .onErrorResume(WebClientResponseException.class, e -> {
                    log.error("Error occurred while calling inventory service", e);
                    // Use the actual error message from the WebClientResponseException
                    return Mono.just(new BaseResponse(new String[]{e.getMessage()}));
                })
                .onErrorResume(throwable -> {
                    log.error("Error occurred while calling inventory service", throwable);
                    return Mono.just(new BaseResponse(new String[]{"Fallback response: Inventory service is unavailable"}));
                });

        return resultMono.flatMap(result -> {
            if (!result.hasErrors()) {
                OrderEntity order = new OrderEntity();
                order.setOrderNumber(UUID.randomUUID().toString());
                order.setOrderItems(orderRequestDTO.getOrderItems().stream()
                        .map(orderItemsRequestDTO -> OrderMapper.mapToOrderItemsEntity(orderItemsRequestDTO, order))
                        .toList());
                return Mono.fromCallable(() -> {
                    this.orderRepository.save(order);
                    String successMessage = "Order saved successfully with order number: " + order.getOrderNumber();
                    return ResponseEntity.ok(successMessage);
                });
            } else {
                String errorMessage = Arrays.toString(result.errorMessage());
                log.error("Error occurred while creating order: {}", errorMessage);
                return Mono.just(ResponseEntity.badRequest().body(errorMessage));
            }
        }).onErrorResume(throwable -> {
            String error = "Exception occurred: " + throwable.getMessage();
            log.error(error, throwable);
            return Mono.just(ResponseEntity.status(500).body(error));
        });
    }

    public ResponseEntity<Object> getAllOrders() {
        OrderMapper orderMapper = new OrderMapper();
        try {
            List<OrderEntity> orders = this.orderRepository.findAll();
            if (orders.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                List<OrderResponseDTO> orderResponseDTOS = orders.stream().map(orderMapper::mapToOrderResponseDTO).toList();
                return ResponseEntity.ok(orderResponseDTOS);
            }
        } catch (DataAccessException e) {
            // Database exception handling
            log.error("DataAccessException occurred while fetching orders", e);
            return ResponseEntity.status(500).body("Database error occurred while fetching orders"); // HTTP 500 Internal Server Error
        } catch (RuntimeException e) {
            // Handling of other unexpected exceptions
            log.error("Unexpected error occurred while fetching orders", e);
            return ResponseEntity.status(500).body("Unexpected error occurred while fetching orders"); // HTTP 500 Internal Server Error
        }
    }
}
