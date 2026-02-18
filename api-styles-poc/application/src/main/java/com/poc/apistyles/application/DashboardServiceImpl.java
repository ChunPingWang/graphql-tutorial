package com.poc.apistyles.application;

import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.model.Order;
import com.poc.apistyles.domain.model.Product;
import com.poc.apistyles.domain.port.inbound.DashboardService;
import com.poc.apistyles.domain.port.outbound.CustomerRepository;
import com.poc.apistyles.domain.port.outbound.OrderRepository;
import com.poc.apistyles.domain.port.outbound.ProductRepository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public DashboardServiceImpl(CustomerRepository customerRepository, OrderRepository orderRepository,
                                ProductRepository productRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Dashboard getDashboard(UUID customerId) {
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
        
        List<Order> recentOrders = orderRepository.findRecentByCustomerId(customerId, 5);
        List<Product> topProducts = productRepository.findTopByOrderFrequency(customerId, 10);
        
        return new Dashboard(customer, recentOrders, topProducts);
    }
}
