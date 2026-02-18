package com.poc.apistyles.domain.model;

import com.poc.apistyles.domain.exception.InvalidEntityException;
import com.poc.apistyles.domain.exception.InvalidStateTransitionException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Order {
    private final UUID id;
    private final UUID customerId;
    private final OrderStatus status;
    private final BigDecimal total;
    private final List<OrderItem> items;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Order(UUID id, UUID customerId, OrderStatus status, BigDecimal total, List<OrderItem> items, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.status = status;
        this.total = total;
        this.items = List.copyOf(items);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Order create(UUID customerId, List<OrderItem> items) {
        if (customerId == null) {
            throw new InvalidEntityException("Order must have a customer");
        }
        if (items == null || items.isEmpty()) {
            throw new InvalidEntityException("Order must have at least one item");
        }
        BigDecimal totalAmount = items.stream()
            .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Order(UUID.randomUUID(), customerId, OrderStatus.CREATED, totalAmount, items, Instant.now(), Instant.now());
    }

    public static Order of(UUID id, UUID customerId, OrderStatus status, BigDecimal total, List<OrderItem> items, Instant createdAt, Instant updatedAt) {
        return new Order(id, customerId, status, total, items != null ? items : List.of(), createdAt, updatedAt);
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
        return transitionTo(OrderStatus.CONFIRMED);
    }

    public Order ship() {
        return transitionTo(OrderStatus.SHIPPED);
    }

    public Order deliver() {
        return transitionTo(OrderStatus.DELIVERED);
    }

    public Order cancel() {
        return transitionTo(OrderStatus.CANCELLED);
    }

    private Order transitionTo(OrderStatus newStatus) {
        if (!canTransitionTo(newStatus)) {
            throw new InvalidStateTransitionException("Order", status.name(), newStatus.name());
        }
        return new Order(id, customerId, newStatus, total, items, createdAt, Instant.now());
    }
}
