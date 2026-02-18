package com.poc.apistyles.domain.model;

import com.poc.apistyles.domain.exception.InvalidEntityException;

import java.time.Instant;
import java.util.UUID;

public class Customer {
    private final UUID id;
    private final String name;
    private final String email;
    private final CustomerTier tier;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Customer(UUID id, String name, String email, CustomerTier tier, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.tier = tier;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Customer create(String name, String email, CustomerTier tier) {
        validateName(name);
        validateEmail(email);
        if (tier == null) {
            tier = CustomerTier.BRONZE;
        }
        return new Customer(UUID.randomUUID(), name, email, tier, Instant.now(), Instant.now());
    }

    public static Customer of(UUID id, String name, String email, CustomerTier tier, Instant createdAt, Instant updatedAt) {
        return new Customer(id, name, email, tier, createdAt, updatedAt);
    }

    public UUID id() { return id; }
    public String name() { return name; }
    public String email() { return email; }
    public CustomerTier tier() { return tier; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }

    public Customer withName(String name) {
        validateName(name);
        return new Customer(this.id, name, this.email, this.tier, this.createdAt, Instant.now());
    }

    public Customer withTier(CustomerTier tier) {
        return new Customer(this.id, this.name, this.email, tier, this.createdAt, Instant.now());
    }

    public Customer withEmail(String email) {
        validateEmail(email);
        return new Customer(this.id, this.name, email, this.tier, this.createdAt, Instant.now());
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidEntityException("Customer name must not be blank");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidEntityException("Customer email must not be blank");
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new InvalidEntityException("Customer email format is invalid: " + email);
        }
    }
}
