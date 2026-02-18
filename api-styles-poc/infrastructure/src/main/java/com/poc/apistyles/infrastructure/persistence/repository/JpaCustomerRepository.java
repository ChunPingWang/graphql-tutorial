package com.poc.apistyles.infrastructure.persistence.repository;

import com.poc.apistyles.infrastructure.persistence.entity.CustomerEntity;
import com.poc.apistyles.domain.model.CustomerTier;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaCustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    List<CustomerEntity> findByTier(CustomerTier tier);
}
