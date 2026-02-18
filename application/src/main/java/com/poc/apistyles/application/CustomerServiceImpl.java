package com.poc.apistyles.application;

import com.poc.apistyles.domain.exception.EntityNotFoundException;
import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.model.CustomerTier;
import com.poc.apistyles.domain.port.inbound.CustomerService;
import com.poc.apistyles.domain.port.outbound.CustomerRepository;

import java.util.List;
import java.util.UUID;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer createCustomer(String name, String email, CustomerTier tier) {
        Customer customer = Customer.create(name, email, tier);
        return customerRepository.save(customer);
    }

    @Override
    public Customer getCustomer(UUID id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Customer", id));
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer updateCustomer(UUID id, String name, String email, CustomerTier tier) {
        Customer existing = getCustomer(id);
        Customer updated = existing.withName(name).withEmail(email).withTier(tier);
        return customerRepository.save(updated);
    }

    @Override
    public void deleteCustomer(UUID id) {
        customerRepository.delete(id);
    }
}
