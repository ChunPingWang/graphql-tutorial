package com.poc.apistyles.domain.port.outbound;

import com.poc.apistyles.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
    Optional<Product> findById(UUID id);
    List<Product> findAll();
    List<Product> findByCategory(String category);
    List<Product> findTopByOrderFrequency(UUID productId, int limit);
    Product save(Product product);
    void delete(UUID id);
    List<Product> saveAll(List<Product> products);
}
