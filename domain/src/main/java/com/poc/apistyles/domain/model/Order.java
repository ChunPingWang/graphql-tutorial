package com.poc.apistyles.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {
    private UUID id;
    private UUID customerId;
    private OrderStatus status;
    private BigDecimal total;
    private List<OrderItem> items;
    private Instant createdAt;
    private Instant updatedAt;

    public Order() {}

    private Order(UUID id, UUID customerId, OrderStatus status, BigDecimal total, List<OrderItem> items, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.total = total;
        this.items = new ArrayList<>(items);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Order create(UUID customerId, List<OrderItem> items) {
        BigDecimal totalAmount = items.stream()
            .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Order(UUID.randomUUID(), customerId, OrderStatus.CREATED, totalAmount, items, Instant.now(), Instant.now());
    }

    public static Order of(UUID id, UUID customerId, OrderStatus status, BigDecimal total, List<OrderItem> items, Instant createdAt, Instant updatedAt) {
        return new Order(id, customerId, status, total, items, createdAt, updatedAt);
    }

    public UUID id() { return id; }
    public UUID customerId() { return customerId; }
    public OrderStatus status() { return status; }
    public BigDecimal total() { return total; }
    public List<OrderItem> items() { return items; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }

    public boolean canTransitionTo(OrderStatus newStatus) {
        return status.allowedTransitions().contains(newStatus);
    }

    public Order confirm() {
        if (!canTransitionTo(OrderStatus.CONFIRMED)) {
            throw new IllegalStateException("Cannot confirm order in status: " + status);
        }
        return new Order(id, customerId, OrderStatus.CONFIRMED, total, items, createdAt, Instant.now());
    }

    public Order ship() {
        if (!canTransitionTo(OrderStatus.SHIPPED)) {
            throw new IllegalStateException("Cannot ship order in status: " + status);
        }
        return new Order(id, customerId, OrderStatus.SHIPPED, total, items, createdAt, Instant.now());
    }

    public Order deliver() {
        if (!canTransitionTo(OrderStatus.DELIVERED)) {
            throw new IllegalStateException("Cannot deliver order in status: " + status);
        }
        return new Order(id, customerId, OrderStatus.DELIVERED, total, items, createdAt, Instant.now());
    }

    public Order cancel() {
        if (!canTransitionTo(OrderStatus.CANCELLED)) {
            throw new IllegalStateException("Cannot cancel order in status: " + status);
        }
        return new Order(id, customerId, OrderStatus.CANCELLED, total, items, createdAt, Instant.now());
    }
}
