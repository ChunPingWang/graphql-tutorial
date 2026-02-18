package com.poc.apistyles.domain.port.inbound;

import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.model.CustomerTier;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    Customer createCustomer(String name, String email, CustomerTier tier);
    Customer getCustomer(UUID id);
    List<Customer> getAllCustomers();
    Customer updateCustomer(UUID id, String name, String email, CustomerTier tier);
    void deleteCustomer(UUID id);
}
