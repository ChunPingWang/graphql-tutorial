package com.poc.apistyles.application;

import com.poc.apistyles.domain.exception.EntityNotFoundException;
import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.model.Order;
import com.poc.apistyles.domain.model.OrderItem;
import com.poc.apistyles.domain.model.OrderStatus;
import com.poc.apistyles.domain.port.inbound.OrderService;
import com.poc.apistyles.domain.port.outbound.CustomerRepository;
import com.poc.apistyles.domain.port.outbound.OrderRepository;
import com.poc.apistyles.domain.port.outbound.ProductRepository;
import com.poc.apistyles.domain.service.OrderDomainService;

import java.util.List;
import java.util.UUID;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderDomainService orderDomainService;

    public OrderServiceImpl(OrderRepository orderRepository, CustomerRepository customerRepository,
                           ProductRepository productRepository, OrderDomainService orderDomainService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderDomainService = orderDomainService;
    }

    @Override
    public Order createOrder(UUID customerId, List<OrderItem> items) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new EntityNotFoundException("Customer", customerId));

        for (OrderItem item : items) {
            productRepository.findById(item.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product", item.productId()));
        }

        Order order = orderDomainService.createOrderWithDiscount(customer, items);
        return orderRepository.save(order);
    }

    @Override
    public Order getOrder(UUID id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Order", id));
    }

    @Override
    public Order confirmOrder(UUID id) {
        Order order = getOrder(id);
        Order confirmed = order.confirm();
        return orderRepository.save(confirmed);
    }

    @Override
    public Order shipOrder(UUID id) {
        Order order = getOrder(id);
        Order shipped = order.ship();
        return orderRepository.save(shipped);
    }

    @Override
    public Order deliverOrder(UUID id) {
        Order order = getOrder(id);
        Order delivered = order.deliver();
        return orderRepository.save(delivered);
    }

    @Override
    public Order cancelOrder(UUID id) {
        Order order = getOrder(id);
        Order cancelled = order.cancel();
        return orderRepository.save(cancelled);
    }

    @Override
    public Order transitionTo(UUID id, OrderStatus targetStatus) {
        return switch (targetStatus) {
            case CONFIRMED -> confirmOrder(id);
            case SHIPPED -> shipOrder(id);
            case DELIVERED -> deliverOrder(id);
            case CANCELLED -> cancelOrder(id);
            default -> throw new IllegalArgumentException("Cannot transition to status: " + targetStatus);
        };
    }
}
