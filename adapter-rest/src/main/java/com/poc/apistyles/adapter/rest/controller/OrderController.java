package com.poc.apistyles.adapter.rest.controller;

import com.poc.apistyles.adapter.rest.dto.OrderRequest;
import com.poc.apistyles.adapter.rest.dto.OrderResponse;
import com.poc.apistyles.domain.model.Order;
import com.poc.apistyles.domain.model.OrderItem;
import com.poc.apistyles.domain.model.OrderStatus;
import com.poc.apistyles.domain.port.inbound.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return List.of();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        Order o = orderService.getOrder(id);
        return ResponseEntity.ok(OrderResponse.from(o));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest request) {
        List<OrderItem> items = request.items().stream()
            .map(item -> OrderItem.create(item.productId(), item.quantity(), item.unitPrice()))
            .toList();
        Order order = orderService.createOrder(request.customerId(), items);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(order));
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable UUID id) {
        Order o = orderService.confirmOrder(id);
        return ResponseEntity.ok(OrderResponse.from(o));
    }

    @PutMapping("/{id}/ship")
    public ResponseEntity<OrderResponse> shipOrder(@PathVariable UUID id) {
        Order o = orderService.shipOrder(id);
        return ResponseEntity.ok(OrderResponse.from(o));
    }

    @PutMapping("/{id}/deliver")
    public ResponseEntity<OrderResponse> deliverOrder(@PathVariable UUID id) {
        Order o = orderService.deliverOrder(id);
        return ResponseEntity.ok(OrderResponse.from(o));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable UUID id) {
        Order o = orderService.cancelOrder(id);
        return ResponseEntity.ok(OrderResponse.from(o));
    }

}
