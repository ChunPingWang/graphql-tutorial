package com.poc.apistyles.infrastructure.config;

import com.poc.apistyles.application.CustomerServiceImpl;
import com.poc.apistyles.application.DashboardServiceImpl;
import com.poc.apistyles.application.OrderServiceImpl;
import com.poc.apistyles.application.ProductServiceImpl;
import com.poc.apistyles.domain.port.inbound.CustomerService;
import com.poc.apistyles.domain.port.inbound.DashboardService;
import com.poc.apistyles.domain.port.inbound.OrderService;
import com.poc.apistyles.domain.port.inbound.ProductService;
import com.poc.apistyles.domain.port.outbound.CustomerRepository;
import com.poc.apistyles.domain.port.outbound.OrderRepository;
import com.poc.apistyles.domain.port.outbound.ProductRepository;
import com.poc.apistyles.domain.service.OrderDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationServiceConfig {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainService();
    }

    @Bean
    public CustomerService customerService(CustomerRepository customerRepository) {
        return new CustomerServiceImpl(customerRepository);
    }

    @Bean
    public ProductService productService(ProductRepository productRepository) {
        return new ProductServiceImpl(productRepository);
    }

    @Bean
    public OrderService orderService(OrderRepository orderRepository,
                                     CustomerRepository customerRepository,
                                     ProductRepository productRepository,
                                     OrderDomainService orderDomainService) {
        return new OrderServiceImpl(orderRepository, customerRepository, productRepository, orderDomainService);
    }

    @Bean
    public DashboardService dashboardService(CustomerRepository customerRepository,
                                             OrderRepository orderRepository,
                                             ProductRepository productRepository) {
        return new DashboardServiceImpl(customerRepository, orderRepository, productRepository);
    }
}
