package com.poc.apistyles.infrastructure.persistence.mapper;

import com.poc.apistyles.domain.model.Product;
import com.poc.apistyles.infrastructure.persistence.entity.ProductEntity;

import java.util.List;

public class ProductMapper {

    public static Product toDomain(ProductEntity entity) {
        if (entity == null) return null;
        return entity.toDomain();
    }

    public static ProductEntity toEntity(Product product) {
        if (product == null) return null;
        return ProductEntity.fromDomain(product);
    }

    public static List<Product> toDomainList(List<ProductEntity> entities) {
        return entities.stream()
            .map(ProductMapper::toDomain)
            .toList();
    }

    public static List<ProductEntity> toEntityList(List<Product> products) {
        return products.stream()
            .map(ProductMapper::toEntity)
            .toList();
    }
}
