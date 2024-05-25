package com.cessadev.mcsv_orders.controllers;

import com.cessadev.mcsv_orders.model.dtos.OrderRequestDTO;
import com.cessadev.mcsv_orders.model.dtos.OrderResponseDTO;
import com.cessadev.mcsv_orders.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create-order")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_USER')")
    public String createOrder(@RequestBody OrderRequestDTO orderRequestDTO) {
        orderService.createOrder(orderRequestDTO);
        return "Order created successfully";
    }

    @GetMapping("/find-all-orders")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<OrderResponseDTO> getAllOrders() {
        return orderService.getAllOrders();
    }
}
