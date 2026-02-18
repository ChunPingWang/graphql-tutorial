package com.poc.apistyles.infrastructure.persistence.mapper;

import com.poc.apistyles.domain.model.Order;
import com.poc.apistyles.domain.model.OrderItem;
import com.poc.apistyles.infrastructure.persistence.entity.OrderEntity;
import com.poc.apistyles.infrastructure.persistence.entity.OrderItemEntity;

import java.util.List;

public class OrderMapper {

    public static Order toDomain(OrderEntity entity) {
        if (entity == null) return null;
        return entity.toDomain();
    }

    public static OrderEntity toEntity(Order order) {
        if (order == null) return null;
        return OrderEntity.fromDomain(order);
    }

    public static List<Order> toDomainList(List<OrderEntity> entities) {
        return entities.stream()
            .map(OrderMapper::toDomain)
            .toList();
    }

    public static List<OrderEntity> toEntityList(List<Order> orders) {
        return orders.stream()
            .map(OrderMapper::toEntity)
            .toList();
    }

    public static OrderItem toDomainItem(OrderItemEntity entity) {
        if (entity == null) return null;
        return entity.toDomain();
    }

    public static OrderItemEntity toEntityItem(OrderItem item) {
        if (item == null) return null;
        return OrderItemEntity.fromDomain(item);
    }
}
