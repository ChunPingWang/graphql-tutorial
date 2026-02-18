package com.poc.apistyles.domain.model;

import com.poc.apistyles.domain.exception.InvalidEntityException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerTest {

    @Test
    void create_withValidData_shouldCreateCustomer() {
        Customer customer = Customer.create("Alice", "alice@example.com", CustomerTier.GOLD);

        assertThat(customer.id()).isNotNull();
        assertThat(customer.name()).isEqualTo("Alice");
        assertThat(customer.email()).isEqualTo("alice@example.com");
        assertThat(customer.tier()).isEqualTo(CustomerTier.GOLD);
        assertThat(customer.createdAt()).isNotNull();
        assertThat(customer.updatedAt()).isNotNull();
    }

    @Test
    void create_withNullTier_shouldDefaultToBronze() {
        Customer customer = Customer.create("Alice", "alice@example.com", null);

        assertThat(customer.tier()).isEqualTo(CustomerTier.BRONZE);
    }

    @Test
    void create_withBlankName_shouldThrow() {
        assertThatThrownBy(() -> Customer.create("", "alice@example.com", CustomerTier.BRONZE))
                .isInstanceOf(InvalidEntityException.class)
                .hasMessageContaining("name must not be blank");
    }

    @Test
    void create_withNullName_shouldThrow() {
        assertThatThrownBy(() -> Customer.create(null, "alice@example.com", CustomerTier.BRONZE))
                .isInstanceOf(InvalidEntityException.class)
                .hasMessageContaining("name must not be blank");
    }

    @Test
    void create_withBlankEmail_shouldThrow() {
        assertThatThrownBy(() -> Customer.create("Alice", "", CustomerTier.BRONZE))
                .isInstanceOf(InvalidEntityException.class)
                .hasMessageContaining("email must not be blank");
    }

    @Test
    void create_withInvalidEmail_shouldThrow() {
        assertThatThrownBy(() -> Customer.create("Alice", "not-an-email", CustomerTier.BRONZE))
                .isInstanceOf(InvalidEntityException.class)
                .hasMessageContaining("email format is invalid");
    }

    @Test
    void withName_shouldReturnNewInstance() {
        Customer original = Customer.create("Alice", "alice@example.com", CustomerTier.BRONZE);
        Customer updated = original.withName("Bob");

        assertThat(updated.name()).isEqualTo("Bob");
        assertThat(updated.id()).isEqualTo(original.id());
        assertThat(original.name()).isEqualTo("Alice");
    }

    @Test
    void withEmail_shouldReturnNewInstance() {
        Customer original = Customer.create("Alice", "alice@example.com", CustomerTier.BRONZE);
        Customer updated = original.withEmail("bob@example.com");

        assertThat(updated.email()).isEqualTo("bob@example.com");
        assertThat(original.email()).isEqualTo("alice@example.com");
    }

    @Test
    void withTier_shouldReturnNewInstance() {
        Customer original = Customer.create("Alice", "alice@example.com", CustomerTier.BRONZE);
        Customer updated = original.withTier(CustomerTier.PLATINUM);

        assertThat(updated.tier()).isEqualTo(CustomerTier.PLATINUM);
        assertThat(original.tier()).isEqualTo(CustomerTier.BRONZE);
    }
}
