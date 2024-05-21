package com.cessadev.mcsv_products.services;

import com.cessadev.mcsv_products.model.dtos.ProductRequestDTO;
import com.cessadev.mcsv_products.model.dtos.ProductResponseDTO;
import com.cessadev.mcsv_products.model.entities.ProductEntity;
import com.cessadev.mcsv_products.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    public void addProduct(ProductRequestDTO productDTO) {
        ProductEntity product = ProductEntity.builder()
                .sku(productDTO.getSku())
                .name(productDTO.getName())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .status(productDTO.getStatus())
                .build();

        productRepository.save(product);

        log.info("Product added: {}", product);
    }

    public List<ProductResponseDTO> findAllProducts() {
        List<ProductEntity> product = productRepository.findAll();
        return product.stream().map(this::toProductDTO).toList();
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
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
