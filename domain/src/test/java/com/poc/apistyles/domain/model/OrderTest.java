package com.poc.apistyles.domain.model;

import com.poc.apistyles.domain.exception.InvalidEntityException;
import com.poc.apistyles.domain.exception.InvalidStateTransitionException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    private final UUID customerId = UUID.randomUUID();

    private List<OrderItem> sampleItems() {
        return List.of(
                OrderItem.create(UUID.randomUUID(), 2, BigDecimal.valueOf(10.00)),
                OrderItem.create(UUID.randomUUID(), 1, BigDecimal.valueOf(25.50))
        );
    }

    @Test
    void create_shouldCalculateTotal() {
        Order order = Order.create(customerId, sampleItems());

        assertThat(order.id()).isNotNull();
        assertThat(order.customerId()).isEqualTo(customerId);
        assertThat(order.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(order.total()).isEqualByComparingTo("45.50");
        assertThat(order.items()).hasSize(2);
    }

    @Test
    void create_withNullCustomerId_shouldThrow() {
        assertThatThrownBy(() -> Order.create(null, sampleItems()))
                .isInstanceOf(InvalidEntityException.class)
                .hasMessageContaining("must have a customer");
    }

    @Test
    void create_withEmptyItems_shouldThrow() {
        assertThatThrownBy(() -> Order.create(customerId, List.of()))
                .isInstanceOf(InvalidEntityException.class)
                .hasMessageContaining("must have at least one item");
    }

    @Test
    void confirm_fromCreated_shouldSucceed() {
        Order order = Order.create(customerId, sampleItems());
        Order confirmed = order.confirm();

        assertThat(confirmed.status()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.status()).isEqualTo(OrderStatus.CREATED);
    }

    @Test
    void ship_fromConfirmed_shouldSucceed() {
        Order order = Order.create(customerId, sampleItems()).confirm();
        Order shipped = order.ship();

        assertThat(shipped.status()).isEqualTo(OrderStatus.SHIPPED);
    }

    @Test
    void deliver_fromShipped_shouldSucceed() {
        Order order = Order.create(customerId, sampleItems()).confirm().ship();
        Order delivered = order.deliver();

        assertThat(delivered.status()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    void cancel_fromCreated_shouldSucceed() {
        Order order = Order.create(customerId, sampleItems());
        Order cancelled = order.cancel();

        assertThat(cancelled.status()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void ship_fromCreated_shouldThrow() {
        Order order = Order.create(customerId, sampleItems());

        assertThatThrownBy(order::ship)
                .isInstanceOf(InvalidStateTransitionException.class)
                .hasMessageContaining("CREATED")
                .hasMessageContaining("SHIPPED");
    }

    @Test
    void confirm_fromDelivered_shouldThrow() {
        Order order = Order.create(customerId, sampleItems()).confirm().ship().deliver();

        assertThatThrownBy(order::confirm)
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    @Test
    void cancel_fromDelivered_shouldThrow() {
        Order order = Order.create(customerId, sampleItems()).confirm().ship().deliver();

        assertThatThrownBy(order::cancel)
                .isInstanceOf(InvalidStateTransitionException.class);
    }

    @Test
    void items_shouldBeImmutable() {
        Order order = Order.create(customerId, sampleItems());

        assertThatThrownBy(() -> order.items().add(OrderItem.create(UUID.randomUUID(), 1, BigDecimal.ONE)))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
