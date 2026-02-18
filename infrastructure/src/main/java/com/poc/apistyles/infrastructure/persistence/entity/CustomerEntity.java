package com.poc.apistyles.infrastructure.persistence.entity;

import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.model.CustomerTier;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerTier tier;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public CustomerEntity() {}

    public CustomerEntity(UUID id, String name, String email, CustomerTier tier, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.tier = tier;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public CustomerTier getTier() { return tier; }
    public void setTier(CustomerTier tier) { this.tier = tier; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Customer toDomain() {
        return Customer.of(id, name, email, tier, createdAt, updatedAt);
    }

    public static CustomerEntity fromDomain(Customer customer) {
        return new CustomerEntity(
            customer.id(),
            customer.name(),
            customer.email(),
            customer.tier(),
            customer.createdAt(),
            customer.updatedAt()
        );
    }
}
