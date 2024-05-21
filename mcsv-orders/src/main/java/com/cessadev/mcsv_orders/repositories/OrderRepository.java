package com.cessadev.mcsv_orders.repositories;

import com.cessadev.mcsv_orders.model.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
}
