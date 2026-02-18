package com.poc.apistyles.domain.service;

import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.model.Order;
import com.poc.apistyles.domain.model.OrderItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

/**
 * Domain service encapsulating cross-aggregate business rules
 * for order creation and tier-based discount calculation.
 */
public class OrderDomainService {

    public Order createOrderWithDiscount(Customer customer, List<OrderItem> items) {
        Order order = Order.create(customer.id(), items);
        BigDecimal discount = calculateTierDiscount(customer, order.total());
        if (discount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountedTotal = order.total().subtract(discount).setScale(2, RoundingMode.HALF_UP);
            return Order.of(order.id(), order.customerId(), order.status(), discountedTotal, order.items(), order.createdAt(), order.updatedAt());
        }
        return order;
    }

    public BigDecimal calculateTierDiscount(Customer customer, BigDecimal orderTotal) {
        double rate = customer.tier().discountRate();
        if (rate <= 0.0) {
            return BigDecimal.ZERO;
        }
        return orderTotal.multiply(BigDecimal.valueOf(rate)).setScale(2, RoundingMode.HALF_UP);
    }
}
