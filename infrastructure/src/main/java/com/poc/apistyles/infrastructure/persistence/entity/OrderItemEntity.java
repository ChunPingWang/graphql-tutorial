package com.poc.apistyles.infrastructure.persistence.entity;

import com.poc.apistyles.domain.model.OrderItem;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;

    public OrderItemEntity() {}

    public OrderItemEntity(UUID id, UUID productId, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public OrderItem toDomain() {
        return new OrderItem(id, productId, quantity, unitPrice);
    }

    public static OrderItemEntity fromDomain(OrderItem orderItem) {
        return new OrderItemEntity(
            orderItem.id(),
            orderItem.productId(),
            orderItem.quantity(),
            orderItem.unitPrice()
        );
    }
}
