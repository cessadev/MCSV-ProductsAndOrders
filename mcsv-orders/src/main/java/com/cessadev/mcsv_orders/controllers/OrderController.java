package com.cessadev.mcsv_orders.controllers;

import com.cessadev.mcsv_orders.model.dtos.OrderRequestDTO;
import com.cessadev.mcsv_orders.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create-order")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Mono<ResponseEntity<String>> createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        return orderService.createOrder(orderRequestDTO);
    }

    @GetMapping("/find-all-orders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> getAllOrders() {
        return orderService.getAllOrders();
    }
}
