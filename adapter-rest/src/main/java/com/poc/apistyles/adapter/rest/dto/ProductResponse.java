package com.poc.apistyles.adapter.rest.dto;

import com.poc.apistyles.domain.model.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(UUID id, String name, BigDecimal price, int stock, String category, Instant createdAt, Instant updatedAt) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.id(),
            product.name(),
            product.price(),
            product.stock(),
            product.category(),
            product.createdAt(),
            product.updatedAt()
        );
    }
}
