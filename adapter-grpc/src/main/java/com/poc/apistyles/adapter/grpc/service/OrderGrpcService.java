package com.poc.apistyles.adapter.grpc.service;

import com.poc.apistyles.adapter.grpc.protobuf.*;
import com.poc.apistyles.domain.model.Order;
import com.poc.apistyles.domain.model.OrderItem;
import com.poc.apistyles.domain.model.OrderStatus;
import com.poc.apistyles.domain.port.inbound.OrderService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@GrpcService
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {

    private final OrderService orderService;

    public OrderGrpcService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void getOrder(GetOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        UUID id = UUID.fromString(request.getId());
        Order order = orderService.getOrder(id);
        responseObserver.onNext(toResponse(order));
        responseObserver.onCompleted();
    }

    @Override
    public void createOrder(CreateOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        UUID customerId = UUID.fromString(request.getCustomerId());
        List<OrderItem> items = request.getItemsList().stream()
                .map(item -> OrderItem.create(
                        UUID.fromString(item.getProductId()),
                        item.getQuantity(),
                        BigDecimal.valueOf(item.getUnitPrice())
                ))
                .collect(Collectors.toList());
        Order order = orderService.createOrder(customerId, items);
        responseObserver.onNext(toResponse(order));
        responseObserver.onCompleted();
    }

    @Override
    public void updateStatus(UpdateStatusRequest request, StreamObserver<OrderResponse> responseObserver) {
        UUID id = UUID.fromString(request.getId());
        OrderStatus status = OrderStatus.valueOf(request.getStatus().toUpperCase());
        Order updatedOrder = orderService.transitionTo(id, status);
        responseObserver.onNext(toResponse(updatedOrder));
        responseObserver.onCompleted();
    }

    @Override
    public void confirmOrder(ConfirmOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        UUID id = UUID.fromString(request.getId());
        Order order = orderService.confirmOrder(id);
        responseObserver.onNext(toResponse(order));
        responseObserver.onCompleted();
    }

    @Override
    public void shipOrder(ShipOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        UUID id = UUID.fromString(request.getId());
        Order order = orderService.shipOrder(id);
        responseObserver.onNext(toResponse(order));
        responseObserver.onCompleted();
    }

    @Override
    public void deliverOrder(DeliverOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        UUID id = UUID.fromString(request.getId());
        Order order = orderService.deliverOrder(id);
        responseObserver.onNext(toResponse(order));
        responseObserver.onCompleted();
    }

    @Override
    public void cancelOrder(CancelOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        UUID id = UUID.fromString(request.getId());
        Order order = orderService.cancelOrder(id);
        responseObserver.onNext(toResponse(order));
        responseObserver.onCompleted();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.items().stream()
                .map(item -> OrderItemResponse.newBuilder()
                        .setProductId(item.productId().toString())
                        .setQuantity(item.quantity())
                        .setUnitPrice(item.unitPrice().doubleValue())
                        .setSubtotal(item.subtotal().doubleValue())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.newBuilder()
                .setId(order.id().toString())
                .setCustomerId(order.customerId().toString())
                .setStatus(order.status().name())
                .setTotal(order.total().doubleValue())
                .addAllItems(itemResponses)
                .setCreatedAt(order.createdAt().toString())
                .setUpdatedAt(order.updatedAt().toString())
                .build();
    }
}
