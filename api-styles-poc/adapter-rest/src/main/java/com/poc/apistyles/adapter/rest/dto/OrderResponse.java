package com.poc.apistyles.adapter.rest.dto;

import com.poc.apistyles.domain.model.Order;
import com.poc.apistyles.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(UUID id, UUID customerId, OrderStatus status, BigDecimal total, List<OrderItemResponse> items, Instant createdAt, Instant updatedAt) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
            order.id(),
            order.customerId(),
            order.status(),
            order.total(),
            order.items().stream().map(OrderItemResponse::from).toList(),
            order.createdAt(),
            order.updatedAt()
        );
    }
}

record OrderItemResponse(UUID id, UUID productId, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
    static OrderItemResponse from(com.poc.apistyles.domain.model.OrderItem item) {
        return new OrderItemResponse(item.id(), item.productId(), item.quantity(), item.unitPrice(), item.subtotal());
    }
}
