package com.poc.apistyles.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItem(UUID id, UUID productId, int quantity, BigDecimal unitPrice) {
    public static OrderItem create(UUID productId, int quantity, BigDecimal unitPrice) {
        return new OrderItem(UUID.randomUUID(), productId, quantity, unitPrice);
    }

    public BigDecimal subtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
