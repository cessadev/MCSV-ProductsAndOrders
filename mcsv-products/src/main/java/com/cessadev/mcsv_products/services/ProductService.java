package com.cessadev.mcsv_products.services;

import com.cessadev.mcsv_products.model.dtos.ProductRequestDTO;
import com.cessadev.mcsv_products.model.dtos.ProductResponseDTO;
import com.cessadev.mcsv_products.model.entities.ProductEntity;
import com.cessadev.mcsv_products.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public ResponseEntity<String> addProduct(ProductRequestDTO productDTO) {
        try {
            ProductEntity product = ProductEntity.builder()
                    .sku(productDTO.getSku())
                    .name(productDTO.getName())
                    .description(productDTO.getDescription())
                    .price(productDTO.getPrice())
                    .status(productDTO.getStatus())
                    .build();

            productRepository.save(product);
            log.info("Product added: {}", product);
            return ResponseEntity.ok("Product with sku " + product.getSku() + " added successfully");
        } catch (DataAccessException e) {
            log.error("DataAccessException occurred while adding product", e);
            return ResponseEntity.status(500).body("Database error occurred while adding product");
        } catch (RuntimeException e) {
            log.error("Unexpected error occurred while adding product", e);
            return ResponseEntity.status(500).body("Unexpected error occurred while adding product");
        }
    }

    public ResponseEntity<Object> findAllProducts() {
        try {
            List<ProductEntity> products = productRepository.findAll();
            if (products.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                List<ProductResponseDTO> productResponseDTOs = products.stream().map(this::toProductDTO).toList();
                return ResponseEntity.ok(productResponseDTOs);
            }
        } catch (DataAccessException e) {
            log.error("DataAccessException occurred while fetching products", e);
            return ResponseEntity.status(500).body("Database error occurred while fetching products");
        } catch (RuntimeException e) {
            log.error("Unexpected error occurred while fetching products", e);
            return ResponseEntity.status(500).body("Unexpected error occurred while fetching products");
        }
    }

    public ResponseEntity<String> deleteProduct(Long productId) {
        try {
            productRepository.deleteById(productId);
            return ResponseEntity.ok("Product deleted successfully");
        } catch (DataAccessException e) {
            log.error("DataAccessException occurred while deleting product", e);
            return ResponseEntity.status(500).body("Database error occurred while deleting product");
        } catch (RuntimeException e) {
            log.error("Unexpected error occurred while deleting product", e);
            return ResponseEntity.status(500).body("Unexpected error occurred while deleting product");
        }
    }

    private ProductResponseDTO toProductDTO(ProductEntity productEntity) {
        return ProductResponseDTO.builder()
                .id(productEntity.getId())
                .sku(productEntity.getSku())
                .name(productEntity.getName())
                .description(productEntity.getDescription())
                .price(productEntity.getPrice())
                .status(productEntity.getStatus())
                .build();
    }
}
