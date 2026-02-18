package com.poc.apistyles.domain.model;

import com.poc.apistyles.domain.exception.InsufficientStockException;
import com.poc.apistyles.domain.exception.InvalidEntityException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Product {
    private final UUID id;
    private final String name;
    private final BigDecimal price;
    private final int stock;
    private final String category;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Product(UUID id, String name, BigDecimal price, int stock, String category, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Product create(String name, BigDecimal price, int stock, String category) {
        if (name == null || name.isBlank()) {
            throw new InvalidEntityException("Product name must not be blank");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidEntityException("Product price must not be negative");
        }
        if (stock < 0) {
            throw new InvalidEntityException("Product stock must not be negative");
        }
        return new Product(UUID.randomUUID(), name, price, stock, category, Instant.now(), Instant.now());
    }

    public static Product of(UUID id, String name, BigDecimal price, int stock, String category, Instant createdAt, Instant updatedAt) {
        return new Product(id, name, price, stock, category, createdAt, updatedAt);
    }

    public UUID id() { return id; }
    public String name() { return name; }
    public BigDecimal price() { return price; }
    public int stock() { return stock; }
    public String category() { return category; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }

    public Product withStock(int newStock) {
        return new Product(id, name, price, newStock, category, createdAt, Instant.now());
    }

    public boolean hasEnoughStock(int quantity) {
        return stock >= quantity;
    }

    public Product reserveStock(int quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new InsufficientStockException(id, quantity, stock);
        }
        return withStock(stock - quantity);
    }

    public Product releaseStock(int quantity) {
        return withStock(stock + quantity);
    }
}
