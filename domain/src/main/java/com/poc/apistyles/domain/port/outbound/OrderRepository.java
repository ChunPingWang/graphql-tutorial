package com.poc.apistyles.domain.port.outbound;

import com.poc.apistyles.domain.model.Order;
import com.poc.apistyles.domain.model.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Optional<Order> findById(UUID id);
    List<Order> findByCustomerId(UUID customerId);
    List<Order> findRecentByCustomerId(UUID customerId, int limit);
    Order save(Order order);
    void delete(UUID id);
    List<Order> findByStatus(OrderStatus status);
}
