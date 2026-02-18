package com.poc.apistyles.test.fixtures;

import com.poc.apistyles.domain.model.Product;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ProductFixture {

    private UUID id = UUID.randomUUID();
    private String name = "Test Product";
    private BigDecimal price = BigDecimal.valueOf(9.99);
    private int stock = 100;
    private String category = "Test Category";
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    private ProductFixture() {
    }

    public static ProductFixture aProduct() {
        return new ProductFixture();
    }

    public ProductFixture withId(UUID id) {
        this.id = id;
        return this;
    }

    public ProductFixture withName(String name) {
        this.name = name;
        return this;
    }

    public ProductFixture withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ProductFixture withStock(int stock) {
        this.stock = stock;
        return this;
    }

    public ProductFixture withCategory(String category) {
        this.category = category;
        return this;
    }

    public ProductFixture withCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public ProductFixture withUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Product build() {
        return Product.of(id, name, price, stock, category, createdAt, updatedAt);
    }
}
