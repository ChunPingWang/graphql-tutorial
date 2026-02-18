package com.poc.apistyles.infrastructure.persistence.repository;

import com.poc.apistyles.infrastructure.persistence.entity.OrderEntity;
import com.poc.apistyles.domain.model.OrderStatus;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaOrderRepository extends JpaRepository<OrderEntity, UUID> {
    List<OrderEntity> findByCustomerId(UUID customerId);
    
    @Query("SELECT o FROM OrderEntity o WHERE o.customerId = :customerId ORDER BY o.createdAt DESC LIMIT :limit")
    List<OrderEntity> findRecentByCustomerId(@Param("customerId") UUID customerId, @Param("limit") int limit);
    
    List<OrderEntity> findByStatus(OrderStatus status);
}
