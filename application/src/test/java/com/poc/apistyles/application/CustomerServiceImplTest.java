package com.poc.apistyles.application;

import com.poc.apistyles.domain.exception.EntityNotFoundException;
import com.poc.apistyles.domain.model.Customer;
import com.poc.apistyles.domain.model.CustomerTier;
import com.poc.apistyles.domain.port.outbound.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(customerRepository);
    }

    @Test
    void createCustomer_shouldSaveAndReturn() {
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

        Customer result = customerService.createCustomer("Alice", "alice@example.com", CustomerTier.GOLD);

        assertThat(result.name()).isEqualTo("Alice");
        assertThat(result.email()).isEqualTo("alice@example.com");
        assertThat(result.tier()).isEqualTo(CustomerTier.GOLD);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void getCustomer_whenExists_shouldReturn() {
        UUID id = UUID.randomUUID();
        Customer customer = Customer.of(id, "Alice", "alice@example.com", CustomerTier.BRONZE, Instant.now(), Instant.now());
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomer(id);

        assertThat(result.id()).isEqualTo(id);
    }

    @Test
    void getCustomer_whenNotExists_shouldThrow() {
        UUID id = UUID.randomUUID();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomer(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void getAllCustomers_shouldDelegateToRepository() {
        List<Customer> customers = List.of(
                Customer.of(UUID.randomUUID(), "A", "a@b.com", CustomerTier.BRONZE, Instant.now(), Instant.now())
        );
        when(customerRepository.findAll()).thenReturn(customers);

        assertThat(customerService.getAllCustomers()).hasSize(1);
    }

    @Test
    void deleteCustomer_shouldDelegateToRepository() {
        UUID id = UUID.randomUUID();
        customerService.deleteCustomer(id);
        verify(customerRepository).delete(id);
    }
}
