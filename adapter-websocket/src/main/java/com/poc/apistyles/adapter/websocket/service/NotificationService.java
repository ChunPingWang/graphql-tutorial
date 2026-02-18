package com.poc.apistyles.adapter.websocket.service;

import com.poc.apistyles.domain.model.Order;
import com.poc.apistyles.domain.port.inbound.OrderService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final OrderService orderService;

    public NotificationService(SimpMessagingTemplate messagingTemplate, OrderService orderService) {
        this.messagingTemplate = messagingTemplate;
        this.orderService = orderService;
    }

    private final Map<String, String> subscriptions = new ConcurrentHashMap<>();

    public void broadcastOrderStatusChange(UUID orderId, Order order) {
        Map<String, Object> message = Map.of(
                "event", "STATUS_CHANGED",
                "orderId", orderId.toString(),
                "status", order.status().name(),
                "total", order.total().toString(),
                "updatedAt", order.updatedAt().toString()
        );
        messagingTemplate.convertAndSend("/topic/order/" + orderId, message);
    }

    public void notifyOrderCreated(UUID orderId, Order order) {
        Map<String, Object> message = Map.of(
                "event", "ORDER_CREATED",
                "orderId", orderId.toString(),
                "status", order.status().name(),
                "total", order.total().toString()
        );
        messagingTemplate.convertAndSend("/topic/order/" + orderId, message);
    }

    public void notifyOrderConfirmed(UUID orderId, Order order) {
        broadcastOrderStatusChange(orderId, order);
    }

    public void notifyOrderShipped(UUID orderId, Order order) {
        broadcastOrderStatusChange(orderId, order);
    }

    public void notifyOrderDelivered(UUID orderId, Order order) {
        broadcastOrderStatusChange(orderId, order);
    }

    public void notifyOrderCancelled(UUID orderId, Order order) {
        broadcastOrderStatusChange(orderId, order);
    }

    public void subscribe(String orderId, String sessionId) {
        subscriptions.put(sessionId, orderId);
    }

    public void unsubscribe(String orderId) {
        subscriptions.entrySet().removeIf(entry -> entry.getValue().equals(orderId));
    }

    public Map<String, Object> getOrderStatus(String orderId) {
        try {
            UUID id = UUID.fromString(orderId);
            Order order = orderService.getOrder(id);
            return Map.of(
                    "event", "ORDER_STATUS",
                    "orderId", order.id().toString(),
                    "status", order.status().name(),
                    "total", order.total().toString(),
                    "customerId", order.customerId().toString(),
                    "createdAt", order.createdAt().toString(),
                    "updatedAt", order.updatedAt().toString()
            );
        } catch (Exception e) {
            return Map.of(
                    "event", "ERROR",
                    "message", "Order not found: " + orderId
            );
        }
    }
}
