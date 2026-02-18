package com.poc.apistyles.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Customer {
    private UUID id;
    private String name;
    private String email;
    private CustomerTier tier;
    private Instant createdAt;
    private Instant updatedAt;

    public Customer() {}

    private Customer(UUID id, String name, String email, CustomerTier tier, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.tier = tier;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Customer create(String name, String email, CustomerTier tier) {
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
        return new Customer(this.id, name, this.email, this.tier, this.createdAt, Instant.now());
    }

    public Customer withTier(CustomerTier tier) {
        return new Customer(this.id, this.name, this.email, tier, this.createdAt, Instant.now());
    }

    public Customer withEmail(String email) {
        return new Customer(this.id, this.name, email, this.tier, this.createdAt, Instant.now());
    }
}
