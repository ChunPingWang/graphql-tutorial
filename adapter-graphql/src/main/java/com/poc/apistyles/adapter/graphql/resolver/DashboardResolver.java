package com.poc.apistyles.adapter.graphql.resolver;

import com.poc.apistyles.domain.model.Product;
import com.poc.apistyles.domain.port.inbound.DashboardService;
import com.poc.apistyles.domain.port.inbound.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class DashboardResolver {

    private final DashboardService dashboardService;
    private final ProductService productService;

    public DashboardResolver(DashboardService dashboardService, ProductService productService) {
        this.dashboardService = dashboardService;
        this.productService = productService;
    }

    @QueryMapping
    public Dashboard dashboard(@Argument UUID customerId) {
        var d = dashboardService.getDashboard(customerId);
        return new Dashboard(
            new CustomerData(d.customer().id(), d.customer().name(), d.customer().email(), d.customer().tier().name(), d.customer().createdAt().toString(), d.customer().updatedAt().toString()),
            d.recentOrders().stream().map(o -> new OrderData(o.id(), o.customerId(), o.status().name(), o.total(), o.createdAt().toString(), o.updatedAt().toString())).toList(),
            d.topProducts().stream().map(p -> new ProductData(p.id(), p.name(), p.price(), p.stock(), p.category(), p.createdAt().toString(), p.updatedAt().toString())).toList()
        );
    }

    @QueryMapping
    public Product product(@Argument UUID id) {
        return productService.getProduct(id);
    }

    @QueryMapping
    public List<Product> products() {
        return productService.getAllProducts();
    }

    public record Dashboard(CustomerData customer, List<OrderData> recentOrders, List<ProductData> topProducts) {}
    public record CustomerData(UUID id, String name, String email, String tier, String createdAt, String updatedAt) {}
    public record OrderData(UUID id, UUID customerId, String status, java.math.BigDecimal total, String createdAt, String updatedAt) {}
    public record ProductData(UUID id, String name, java.math.BigDecimal price, int stock, String category, String createdAt, String updatedAt) {}
}
