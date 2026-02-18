package com.poc.apistyles.domain.port.inbound;

import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.model.Order;
import com.poc.apistyles.domain.model.Product;

import java.util.List;
import java.util.UUID;

public interface DashboardService {
    Dashboard getDashboard(UUID customerId);

    record Dashboard(
        Customer customer,
        List<Order> recentOrders,
        List<Product> topProducts
    ) {}
}
