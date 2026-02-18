package com.poc.apistyles.adapter.graphql.resolver;

import com.poc.apistyles.domain.model.Order;
import com.poc.apistyles.domain.model.OrderItem;
import com.poc.apistyles.domain.model.OrderStatus;
import com.poc.apistyles.domain.port.inbound.OrderService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class OrderResolver {

    private final OrderService orderService;

    public OrderResolver(OrderService orderService) {
        this.orderService = orderService;
    }

    @QueryMapping
    public Order order(@Argument UUID id) {
        return orderService.getOrder(id);
    }

    @QueryMapping
    public List<Order> orders() {
        return List.of();
    }

    @MutationMapping
    public Order createOrder(@Argument OrderInput input) {
        List<OrderItem> items = input.items().stream()
            .map(item -> OrderItem.create(item.productId(), item.quantity(), item.unitPrice()))
            .toList();
        return orderService.createOrder(input.customerId(), items);
    }

    @MutationMapping
    public Order updateOrderStatus(@Argument UUID id, @Argument OrderStatus status) {
        return switch (status) {
            case CONFIRMED -> orderService.confirmOrder(id);
            case SHIPPED -> orderService.shipOrder(id);
            case DELIVERED -> orderService.deliverOrder(id);
            case CANCELLED -> orderService.cancelOrder(id);
            default -> throw new IllegalArgumentException("Invalid status: " + status);
        };
    }

    public record OrderInput(UUID customerId, List<OrderItemInput> items) {}
    public record OrderItemInput(UUID productId, int quantity, java.math.BigDecimal unitPrice) {}
}
