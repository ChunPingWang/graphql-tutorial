package com.poc.apistyles.adapter.soap.endpoint;

import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.model.CustomerTier;
import com.poc.apistyles.domain.port.inbound.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.SoapFault;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Endpoint
public class CustomerSoapEndpoint {

    private static final String NAMESPACE_URI = "http://poc.apistyles.com/soap";

    @Autowired
    private CustomerService customerService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetCustomerRequest")
    @ResponsePayload
    public Map<String, Object> getCustomer(@RequestPayload Map<String, String> request) {
        UUID id = UUID.fromString(request.get("id"));
        Customer customer = customerService.getCustomer(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", customer.id().toString());
        response.put("name", customer.name());
        response.put("email", customer.email());
        response.put("tier", customer.tier().name());
        response.put("createdAt", customer.createdAt().toString());
        response.put("updatedAt", customer.updatedAt().toString());
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "CreateCustomerRequest")
    @ResponsePayload
    public Map<String, Object> createCustomer(@RequestPayload Map<String, String> request) {
        CustomerTier tier = CustomerTier.valueOf(request.get("tier").toUpperCase());
        Customer customer = customerService.createCustomer(
                request.get("name"), 
                request.get("email"), 
                tier
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", customer.id().toString());
        response.put("name", customer.name());
        response.put("email", customer.email());
        response.put("tier", customer.tier().name());
        response.put("createdAt", customer.createdAt().toString());
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ListCustomersRequest")
    @ResponsePayload
    public Map<String, Object> listCustomers(@RequestPayload Map<String, String> request) {
        List<Customer> customers = customerService.getAllCustomers();
        
        List<Map<String, String>> customerMaps = customers.stream()
                .map(this::toMap)
                .collect(java.util.stream.Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("customers", customerMaps);
        response.put("total", customerMaps.size());
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "UpdateCustomerRequest")
    @ResponsePayload
    public Map<String, Object> updateCustomer(@RequestPayload Map<String, String> request) {
        UUID id = UUID.fromString(request.get("id"));
        CustomerTier tier = CustomerTier.valueOf(request.get("tier").toUpperCase());
        Customer customer = customerService.updateCustomer(
                id, 
                request.get("name"), 
                request.get("email"), 
                tier
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", customer.id().toString());
        response.put("name", customer.name());
        response.put("email", customer.email());
        response.put("tier", customer.tier().name());
        response.put("updatedAt", customer.updatedAt().toString());
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "DeleteCustomerRequest")
    @ResponsePayload
    public Map<String, Object> deleteCustomer(@RequestPayload Map<String, String> request) {
        UUID id = UUID.fromString(request.get("id"));
        customerService.deleteCustomer(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return response;
    }

    private Map<String, String> toMap(Customer customer) {
        Map<String, String> map = new HashMap<>();
        map.put("id", customer.id().toString());
        map.put("name", customer.name());
        map.put("email", customer.email());
        map.put("tier", customer.tier().name());
        map.put("createdAt", customer.createdAt().toString());
        return map;
    }
}
