package com.poc.apistyles.adapter.graphql.resolver;

import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.model.CustomerTier;
import com.poc.apistyles.domain.port.inbound.CustomerService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class CustomerResolver {

    private final CustomerService customerService;

    public CustomerResolver(CustomerService customerService) {
        this.customerService = customerService;
    }

    @QueryMapping
    public Customer customer(@Argument UUID id) {
        return customerService.getCustomer(id);
    }

    @QueryMapping
    public List<Customer> customers() {
        return customerService.getAllCustomers();
    }

    @MutationMapping
    public Customer createCustomer(@Argument CustomerInput input) {
        return customerService.createCustomer(input.name(), input.email(), input.tier());
    }

    @MutationMapping
    public Customer updateCustomer(@Argument UUID id, @Argument CustomerInput input) {
        return customerService.updateCustomer(id, input.name(), input.email(), input.tier());
    }

    @MutationMapping
    public boolean deleteCustomer(@Argument UUID id) {
        customerService.deleteCustomer(id);
        return true;
    }

    public record CustomerInput(String name, String email, CustomerTier tier) {}
}
