package com.poc.apistyles.infrastructure.persistence.repository;

import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.model.CustomerTier;
import com.poc.apistyles.domain.port.outbound.CustomerRepository;
import com.poc.apistyles.infrastructure.persistence.entity.CustomerEntity;
import com.poc.apistyles.infrastructure.persistence.mapper.CustomerMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepositoryImpl implements CustomerRepository {

    private final JpaCustomerRepository jpaRepository;

    public CustomerRepositoryImpl(JpaCustomerRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return jpaRepository.findById(id).map(CustomerMapper::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return CustomerMapper.toDomainList(jpaRepository.findAll());
    }

    @Override
    public List<Customer> findByTier(CustomerTier tier) {
        return jpaRepository.findByTier(tier).stream()
            .map(CustomerMapper::toDomain)
            .toList();
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = CustomerMapper.toEntity(customer);
        if (customer.id() == null) {
            return CustomerMapper.toDomain(jpaRepository.save(entity));
        }
        return CustomerMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}
