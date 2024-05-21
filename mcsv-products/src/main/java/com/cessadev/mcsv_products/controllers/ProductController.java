package com.cessadev.mcsv_products.controllers;

import com.cessadev.mcsv_products.model.dtos.ProductRequestDTO;
import com.cessadev.mcsv_products.model.dtos.ProductResponseDTO;
import com.cessadev.mcsv_products.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/find-all-products")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponseDTO> getAllProducts() {
        return this.productService.findAllProducts();
    }

    @PostMapping("/create-product")
    @ResponseStatus(HttpStatus.CREATED)
    public void createProduct(@RequestBody ProductRequestDTO productRequestDTO) {
        this.productService.addProduct(productRequestDTO);
    }

    @DeleteMapping("/delete-product-by-id/{productId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteProduct(@PathVariable Long productId) {
        this.productService.deleteProduct(productId);
    }
}
