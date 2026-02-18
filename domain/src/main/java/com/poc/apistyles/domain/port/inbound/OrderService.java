package com.poc.apistyles.domain.port.inbound;

import com.poc.apistyles.domain.model.Order;
import com.poc.apistyles.domain.model.OrderItem;
import com.poc.apistyles.domain.model.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order createOrder(UUID customerId, List<OrderItem> items);
    Order getOrder(UUID id);
    Order confirmOrder(UUID id);
    Order shipOrder(UUID id);
    Order deliverOrder(UUID id);
    Order cancelOrder(UUID id);
    Order transitionTo(UUID id, OrderStatus targetStatus);
}
