package com.poc.apistyles.test.fixtures;

import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.model.CustomerTier;

import java.time.Instant;
import java.util.UUID;

public class CustomerFixture {

    private UUID id = UUID.randomUUID();
    private String name = "Test Customer";
    private String email = "test@example.com";
    private CustomerTier tier = CustomerTier.BRONZE;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    private CustomerFixture() {
    }

    public static CustomerFixture aCustomer() {
        return new CustomerFixture();
    }

    public CustomerFixture withId(UUID id) {
        this.id = id;
        return this;
    }

    public CustomerFixture withName(String name) {
        this.name = name;
        return this;
    }

    public CustomerFixture withEmail(String email) {
        this.email = email;
        return this;
    }

    public CustomerFixture withTier(CustomerTier tier) {
        this.tier = tier;
        return this;
    }

    public CustomerFixture withCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public CustomerFixture withUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public Customer build() {
        return Customer.of(id, name, email, tier, createdAt, updatedAt);
    }
}
