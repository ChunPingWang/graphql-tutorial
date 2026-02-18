package com.poc.apistyles.adapter.soap.endpoint;

import com.poc.apistyles.domain.model.Order;
import com.poc.apistyles.domain.model.OrderItem;
import com.poc.apistyles.domain.model.OrderStatus;
import com.poc.apistyles.domain.port.inbound.OrderService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Endpoint
public class OrderSoapEndpoint {

    private static final String NAMESPACE_URI = "http://poc.apistyles.com/soap";

    private final OrderService orderService;

    public OrderSoapEndpoint(OrderService orderService) {
        this.orderService = orderService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetOrderRequest")
    @ResponsePayload
    public Map<String, Object> getOrder(@RequestPayload Map<String, String> request) {
        UUID id = UUID.fromString(request.get("id"));
        Order order = orderService.getOrder(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", order.id().toString());
        response.put("customerId", order.customerId().toString());
        response.put("status", order.status().name());
        response.put("total", order.total().doubleValue());
        response.put("createdAt", order.createdAt().toString());
        response.put("updatedAt", order.updatedAt().toString());
        
        List<Map<String, Object>> itemMaps = order.items().stream()
                .map(this::toMap)
                .collect(java.util.stream.Collectors.toList());
        response.put("items", itemMaps);
        
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreateOrderRequest")
    @ResponsePayload
    public Map<String, Object> createOrder(@RequestPayload Map<String, Object> request) {
        UUID customerId = UUID.fromString((String) request.get("customerId"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemsData = (List<Map<String, Object>>) request.get("items");
        List<OrderItem> items = itemsData.stream()
                .map(item -> OrderItem.create(
                        UUID.fromString((String) item.get("productId")),
                        ((Number) item.get("quantity")).intValue(),
                        BigDecimal.valueOf(((Number) item.get("unitPrice")).doubleValue())
                ))
                .collect(java.util.stream.Collectors.toList());
        
        Order order = orderService.createOrder(customerId, items);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", order.id().toString());
        response.put("status", order.status().name());
        response.put("total", order.total().doubleValue());
        response.put("createdAt", order.createdAt().toString());
        
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ConfirmOrderRequest")
    @ResponsePayload
    public Map<String, Object> confirmOrder(@RequestPayload Map<String, String> request) {
        UUID id = UUID.fromString(request.get("id"));
        Order order = orderService.confirmOrder(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", order.id().toString());
        response.put("status", order.status().name());
        response.put("updatedAt", order.updatedAt().toString());
        
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ShipOrderRequest")
    @ResponsePayload
    public Map<String, Object> shipOrder(@RequestPayload Map<String, String> request) {
        UUID id = UUID.fromString(request.get("id"));
        Order order = orderService.shipOrder(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", order.id().toString());
        response.put("status", order.status().name());
        response.put("updatedAt", order.updatedAt().toString());
        
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "DeliverOrderRequest")
    @ResponsePayload
    public Map<String, Object> deliverOrder(@RequestPayload Map<String, String> request) {
        UUID id = UUID.fromString(request.get("id"));
        Order order = orderService.deliverOrder(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", order.id().toString());
        response.put("status", order.status().name());
        response.put("updatedAt", order.updatedAt().toString());
        
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CancelOrderRequest")
    @ResponsePayload
    public Map<String, Object> cancelOrder(@RequestPayload Map<String, String> request) {
        UUID id = UUID.fromString(request.get("id"));
        Order order = orderService.cancelOrder(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", order.id().toString());
        response.put("status", order.status().name());
        response.put("updatedAt", order.updatedAt().toString());
        
        return response;
    }

    private Map<String, Object> toMap(OrderItem item) {
        Map<String, Object> map = new HashMap<>();
        map.put("productId", item.productId().toString());
        map.put("quantity", item.quantity());
        map.put("unitPrice", item.unitPrice().doubleValue());
        map.put("subtotal", item.subtotal().doubleValue());
        return map;
    }
}
