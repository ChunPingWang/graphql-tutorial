package com.poc.apistyles.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderStatusTest {

    @Test
    void created_canTransitionToConfirmedOrCancelled() {
        assertThat(OrderStatus.CREATED.allowedTransitions())
                .containsExactlyInAnyOrder(OrderStatus.CONFIRMED, OrderStatus.CANCELLED);
    }

    @Test
    void confirmed_canTransitionToShippedOrCancelled() {
        assertThat(OrderStatus.CONFIRMED.allowedTransitions())
                .containsExactlyInAnyOrder(OrderStatus.SHIPPED, OrderStatus.CANCELLED);
    }

    @Test
    void shipped_canTransitionToDeliveredOrCancelled() {
        assertThat(OrderStatus.SHIPPED.allowedTransitions())
                .containsExactlyInAnyOrder(OrderStatus.DELIVERED, OrderStatus.CANCELLED);
    }

    @Test
    void delivered_hasNoTransitions() {
        assertThat(OrderStatus.DELIVERED.allowedTransitions()).isEmpty();
    }

    @Test
    void cancelled_hasNoTransitions() {
        assertThat(OrderStatus.CANCELLED.allowedTransitions()).isEmpty();
    }
}
