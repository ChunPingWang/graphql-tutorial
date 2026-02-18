package com.poc.apistyles.domain.port.outbound;

import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.model.CustomerTier;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Optional<Customer> findById(UUID id);
    List<Customer> findAll();
    List<Customer> findByTier(CustomerTier tier);
    Customer save(Customer customer);
    void delete(UUID id);
}
