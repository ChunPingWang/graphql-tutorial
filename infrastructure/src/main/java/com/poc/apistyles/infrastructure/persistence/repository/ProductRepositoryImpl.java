package com.poc.apistyles.infrastructure.persistence.repository;

import com.poc.apistyles.domain.model.Product;
import com.poc.apistyles.domain.port.outbound.ProductRepository;
import com.poc.apistyles.infrastructure.persistence.entity.ProductEntity;
import com.poc.apistyles.infrastructure.persistence.mapper.ProductMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaRepository;

    public ProductRepositoryImpl(JpaProductRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return jpaRepository.findById(id).map(ProductMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return ProductMapper.toDomainList(jpaRepository.findAll());
    }

    @Override
    public List<Product> findByCategory(String category) {
        return jpaRepository.findByCategory(category).stream()
            .map(ProductMapper::toDomain)
            .toList();
    }

    @Override
    public List<Product> findTopByOrderFrequency(UUID customerId, int limit) {
        return jpaRepository.findTopByOrderFrequency(customerId, limit).stream()
            .map(ProductMapper::toDomain)
            .toList();
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = ProductMapper.toEntity(product);
        if (product.id() == null) {
            return ProductMapper.toDomain(jpaRepository.save(entity));
        }
        return ProductMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Product> saveAll(List<Product> products) {
        List<ProductEntity> entities = ProductMapper.toEntityList(products);
        return ProductMapper.toDomainList(jpaRepository.saveAll(entities));
    }
}
