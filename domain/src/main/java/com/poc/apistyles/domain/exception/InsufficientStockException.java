package com.poc.apistyles.domain.exception;

import java.util.UUID;

public final class InsufficientStockException extends DomainException {

    public InsufficientStockException(UUID productId, int requested, int available) {
        super("Insufficient stock for product %s: requested %d, available %d".formatted(productId, requested, available));
    }
}
