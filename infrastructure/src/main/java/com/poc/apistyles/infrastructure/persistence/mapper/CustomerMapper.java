package com.poc.apistyles.infrastructure.persistence.mapper;

import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.infrastructure.persistence.entity.CustomerEntity;

import java.util.List;

public class CustomerMapper {

    public static Customer toDomain(CustomerEntity entity) {
        if (entity == null) return null;
        return entity.toDomain();
    }

    public static CustomerEntity toEntity(Customer customer) {
        if (customer == null) return null;
        return CustomerEntity.fromDomain(customer);
    }

    public static List<Customer> toDomainList(List<CustomerEntity> entities) {
        return entities.stream()
            .map(CustomerMapper::toDomain)
            .toList();
    }

    public static List<CustomerEntity> toEntityList(List<Customer> customers) {
        return customers.stream()
            .map(CustomerMapper::toEntity)
            .toList();
    }
}
