package com.poc.apistyles.adapter.rest.dto;

import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.model.CustomerTier;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(UUID id, String name, String email, CustomerTier tier, Instant createdAt, Instant updatedAt) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
            customer.id(),
            customer.name(),
            customer.email(),
            customer.tier(),
            customer.createdAt(),
            customer.updatedAt()
        );
    }
}
