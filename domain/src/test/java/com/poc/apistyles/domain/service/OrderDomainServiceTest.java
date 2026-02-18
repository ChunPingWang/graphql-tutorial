package com.poc.apistyles.domain.service;

import com.poc.apistyles.domain.model.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OrderDomainServiceTest {

    private final OrderDomainService service = new OrderDomainService();

    private List<OrderItem> sampleItems() {
        return List.of(
                OrderItem.create(UUID.randomUUID(), 2, BigDecimal.valueOf(50.00))
        );
    }

    @Test
    void createOrderWithDiscount_bronzeCustomer_noDiscount() {
        Customer customer = Customer.create("Alice", "alice@example.com", CustomerTier.BRONZE);
        Order order = service.createOrderWithDiscount(customer, sampleItems());

        assertThat(order.total()).isEqualByComparingTo("100.00");
    }

    @Test
    void createOrderWithDiscount_goldCustomer_appliesDiscount() {
        Customer customer = Customer.create("Alice", "alice@example.com", CustomerTier.GOLD);
        Order order = service.createOrderWithDiscount(customer, sampleItems());

        // 100.00 * 0.10 = 10.00 discount -> 90.00
        assertThat(order.total()).isEqualByComparingTo("90.00");
    }

    @Test
    void createOrderWithDiscount_platinumCustomer_appliesDiscount() {
        Customer customer = Customer.create("Alice", "alice@example.com", CustomerTier.PLATINUM);
        Order order = service.createOrderWithDiscount(customer, sampleItems());

        // 100.00 * 0.15 = 15.00 discount -> 85.00
        assertThat(order.total()).isEqualByComparingTo("85.00");
    }

    @Test
    void calculateTierDiscount_shouldReturnCorrectAmount() {
        Customer customer = Customer.create("Alice", "alice@example.com", CustomerTier.SILVER);
        BigDecimal discount = service.calculateTierDiscount(customer, BigDecimal.valueOf(200.00));

        assertThat(discount).isEqualByComparingTo("10.00");
    }
}
