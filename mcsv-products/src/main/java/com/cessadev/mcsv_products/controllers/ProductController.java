package com.cessadev.mcsv_products.controllers;

import com.cessadev.mcsv_products.model.dtos.ProductRequestDTO;
import com.cessadev.mcsv_products.model.dtos.ProductResponseDTO;
import com.cessadev.mcsv_products.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/find-all-products")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Object> getAllProducts() {
        return productService.findAllProducts();
    }

    @PostMapping("/create-product")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> createProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        return productService.addProduct(productRequestDTO);
    }

    @DeleteMapping("/delete-product-by-id/{productId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {
        return productService.deleteProduct(productId);
    }
}
