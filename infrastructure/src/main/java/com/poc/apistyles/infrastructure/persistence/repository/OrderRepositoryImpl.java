package com.poc.apistyles.infrastructure.persistence.repository;

import com.poc.apistyles.domain.model.Order;
import com.poc.apistyles.domain.model.OrderStatus;
import com.poc.apistyles.domain.port.outbound.OrderRepository;
import com.poc.apistyles.infrastructure.persistence.entity.OrderEntity;
import com.poc.apistyles.infrastructure.persistence.mapper.OrderMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaRepository;

    public OrderRepositoryImpl(JpaOrderRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id).map(OrderMapper::toDomain);
    }

    @Override
    public List<Order> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerId(customerId).stream()
            .map(OrderMapper::toDomain)
            .toList();
    }

    @Override
    public List<Order> findRecentByCustomerId(UUID customerId, int limit) {
        return jpaRepository.findRecentByCustomerId(customerId, limit).stream()
            .map(OrderMapper::toDomain)
            .toList();
    }

    @Override
    public Order save(Order order) {
        OrderEntity entity = OrderMapper.toEntity(order);
        return OrderMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return jpaRepository.findByStatus(status).stream()
            .map(OrderMapper::toDomain)
            .toList();
    }
}
