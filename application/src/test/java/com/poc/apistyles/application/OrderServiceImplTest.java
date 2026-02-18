package com.poc.apistyles.application;

import com.poc.apistyles.domain.exception.EntityNotFoundException;
import com.poc.apistyles.domain.model.*;
import com.poc.apistyles.domain.port.outbound.CustomerRepository;
import com.poc.apistyles.domain.port.outbound.OrderRepository;
import com.poc.apistyles.domain.port.outbound.ProductRepository;
import com.poc.apistyles.domain.service.OrderDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private ProductRepository productRepository;

    private final OrderDomainService orderDomainService = new OrderDomainService();
    private OrderServiceImpl orderService;

    private final UUID customerId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, customerRepository, productRepository, orderDomainService);
    }

    private List<OrderItem> sampleItems() {
        return List.of(OrderItem.create(productId, 2, BigDecimal.valueOf(25.00)));
    }

    @Test
    void createOrder_shouldValidateCustomerAndProducts() {
        Customer customer = Customer.of(customerId, "A", "a@b.com", CustomerTier.BRONZE, Instant.now(), Instant.now());
        Product product = Product.of(productId, "P", BigDecimal.TEN, 100, "C", Instant.now(), Instant.now());

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.createOrder(customerId, sampleItems());

        assertThat(result.customerId()).isEqualTo(customerId);
        assertThat(result.status()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void createOrder_customerNotFound_shouldThrow() {
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(customerId, sampleItems()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Customer");
    }

    @Test
    void confirmOrder_shouldTransitionState() {
        UUID orderId = UUID.randomUUID();
        Order order = Order.of(orderId, customerId, OrderStatus.CREATED, BigDecimal.TEN, List.of(), Instant.now(), Instant.now());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.confirmOrder(orderId);

        assertThat(result.status()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void getOrder_notFound_shouldThrow() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder(orderId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Order");
    }
}
