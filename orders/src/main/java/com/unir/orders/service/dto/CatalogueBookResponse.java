package com.unir.orders.service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CatalogueBookResponse {

    private Long id;
    private String title;
    private Boolean visible;
    private BigDecimal price;
}

