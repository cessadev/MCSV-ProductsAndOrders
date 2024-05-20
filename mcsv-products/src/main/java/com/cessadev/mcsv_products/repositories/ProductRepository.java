package com.cessadev.mcsv_products.repositories;

import com.cessadev.mcsv_products.model.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
