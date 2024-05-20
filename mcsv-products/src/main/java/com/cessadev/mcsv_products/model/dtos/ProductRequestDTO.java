package com.cessadev.mcsv_products.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequestDTO {

    private String sku;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean status;
}
