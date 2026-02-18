package com.poc.apistyles.infrastructure.persistence.repository;

import com.poc.apistyles.infrastructure.persistence.entity.ProductEntity;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, UUID> {
    List<ProductEntity> findByCategory(String category);
    
    @Query(value = """
        SELECT p.* FROM products p
        LEFT JOIN order_items oi ON p.id = oi.product_id
        LEFT JOIN orders o ON oi.order_id = o.id
        WHERE o.customer_id = :customerId
        GROUP BY p.id
        ORDER BY COUNT(oi.id) DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<ProductEntity> findTopByOrderFrequency(@Param("customerId") UUID customerId, @Param("limit") int limit);
}
