package com.poc.apistyles.adapter.rest.dto;

import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.model.Order;
import com.poc.apistyles.domain.model.Product;

import java.util.List;

public record DashboardResponse(
    CustomerResponse customer,
    List<OrderResponse> recentOrders,
    List<ProductResponse> topProducts
) {
    public static DashboardResponse from(com.poc.apistyles.domain.port.inbound.DashboardService.Dashboard dashboard) {
        return new DashboardResponse(
            CustomerResponse.from(dashboard.customer()),
            dashboard.recentOrders().stream().map(OrderResponse::from).toList(),
            dashboard.topProducts().stream().map(ProductResponse::from).toList()
        );
    }
}
