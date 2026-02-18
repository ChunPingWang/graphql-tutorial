package com.poc.apistyles.adapter.grpc.service;

import com.poc.apistyles.adapter.grpc.protobuf.*;
import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.model.CustomerTier;
import com.poc.apistyles.domain.port.inbound.CustomerService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@GrpcService
public class CustomerGrpcService extends CustomerServiceGrpc.CustomerServiceImplBase {

    private final CustomerService customerService;

    public CustomerGrpcService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void getCustomer(GetCustomerRequest request, StreamObserver<CustomerResponse> responseObserver) {
        UUID id = UUID.fromString(request.getId());
        Customer customer = customerService.getCustomer(id);
        responseObserver.onNext(toResponse(customer));
        responseObserver.onCompleted();
    }

    @Override
    public void createCustomer(CreateCustomerRequest request, StreamObserver<CustomerResponse> responseObserver) {
        CustomerTier tier = CustomerTier.valueOf(request.getTier().toUpperCase());
        Customer customer = customerService.createCustomer(request.getName(), request.getEmail(), tier);
        responseObserver.onNext(toResponse(customer));
        responseObserver.onCompleted();
    }

    @Override
    public void listCustomers(ListCustomersRequest request, StreamObserver<ListCustomersResponse> responseObserver) {
        List<Customer> customers = customerService.getAllCustomers();
        List<CustomerResponse> responses = customers.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        responseObserver.onNext(ListCustomersResponse.newBuilder()
                .addAllCustomers(responses)
                .setTotal(responses.size())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateCustomer(UpdateCustomerRequest request, StreamObserver<CustomerResponse> responseObserver) {
        UUID id = UUID.fromString(request.getId());
        CustomerTier tier = CustomerTier.valueOf(request.getTier().toUpperCase());
        Customer customer = customerService.updateCustomer(id, request.getName(), request.getEmail(), tier);
        responseObserver.onNext(toResponse(customer));
        responseObserver.onCompleted();
    }

    @Override
    public void deleteCustomer(DeleteCustomerRequest request, StreamObserver<DeleteCustomerResponse> responseObserver) {
        UUID id = UUID.fromString(request.getId());
        customerService.deleteCustomer(id);
        responseObserver.onNext(DeleteCustomerResponse.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
    }

    private CustomerResponse toResponse(Customer customer) {
        return CustomerResponse.newBuilder()
                .setId(customer.id().toString())
                .setName(customer.name())
                .setEmail(customer.email())
                .setTier(customer.tier().name())
                .setCreatedAt(customer.createdAt().toString())
                .setUpdatedAt(customer.updatedAt().toString())
                .build();
    }
}
