package com.poc.apistyles.adapter.websocket.handler;

import com.poc.apistyles.adapter.websocket.service.NotificationService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class OrderStatusHandler {

    private final NotificationService notificationService;

    public OrderStatusHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @MessageMapping("/order/subscribe/{orderId}")
    @SendTo("/topic/order/{orderId}")
    public Map<String, Object> subscribeToOrder(@DestinationVariable String orderId) {
        return Map.of(
                "event", "SUBSCRIBED",
                "orderId", orderId,
                "message", "Subscribed to order status updates"
        );
    }

    @MessageMapping("/order/unsubscribe/{orderId}")
    public void unsubscribeFromOrder(@DestinationVariable String orderId) {
        notificationService.unsubscribe(orderId);
    }

    @MessageMapping("/order/status/request/{orderId}")
    @SendTo("/topic/order/{orderId}")
    public Map<String, Object> requestOrderStatus(@DestinationVariable String orderId) {
        return notificationService.getOrderStatus(orderId);
    }
}
