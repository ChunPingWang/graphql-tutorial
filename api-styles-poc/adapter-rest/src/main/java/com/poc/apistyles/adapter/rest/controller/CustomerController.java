package com.poc.apistyles.adapter.rest.controller;

import com.poc.apistyles.adapter.rest.dto.CustomerRequest;
import com.poc.apistyles.adapter.rest.dto.CustomerResponse;
import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.port.inbound.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers().stream()
            .map(CustomerResponse::from)
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable UUID id) {
        Customer c = customerService.getCustomer(id);
        return ResponseEntity.ok(CustomerResponse.from(c));
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request) {
        Customer c = customerService.createCustomer(request.name(), request.email(), request.tier());
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerResponse.from(c));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable UUID id, @RequestBody CustomerRequest request) {
        Customer c = customerService.updateCustomer(id, request.name(), request.email(), request.tier());
        return ResponseEntity.ok(CustomerResponse.from(c));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
