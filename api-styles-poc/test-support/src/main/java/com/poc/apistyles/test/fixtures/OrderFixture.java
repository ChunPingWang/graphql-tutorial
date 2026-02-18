package com.poc.apistyles.test.fixtures;

import com.poc.apistyles.domain.model.Order;
import com.poc.apistyles.domain.model.OrderItem;
import com.poc.apistyles.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderFixture {

    private UUID id = UUID.randomUUID();
    private UUID customerId = UUID.randomUUID();
    private OrderStatus status = OrderStatus.CREATED;
    private BigDecimal total = BigDecimal.ZERO;
    private List<OrderItem> items = new ArrayList<>();
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    private OrderFixture() {
    }

    public static OrderFixture anOrder() {
        return new OrderFixture();
    }

    public OrderFixture withId(UUID id) {
        this.id = id;
        return this;
    }

    public OrderFixture withCustomerId(UUID customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderFixture withStatus(OrderStatus status) {
        this.status = status;
        return this;
    }

    public OrderFixture withTotal(BigDecimal total) {
        this.total = total;
        return this;
    }

    public OrderFixture withItems(List<OrderItem> items) {
        this.items = new ArrayList<>(items);
        this.total = items.stream()
            .map(OrderItem::subtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return this;
    }

    public OrderFixture addItem(OrderItem item) {
        this.items.add(item);
        this.total = this.total.add(item.subtotal());
        return this;
    }

    public OrderFixture withCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public OrderFixture withUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Order build() {
        return Order.of(id, customerId, status, total, new ArrayList<>(items), createdAt, updatedAt);
    }
}
