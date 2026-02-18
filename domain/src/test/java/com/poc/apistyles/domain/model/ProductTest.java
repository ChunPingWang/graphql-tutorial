package com.poc.apistyles.domain.model;

import com.poc.apistyles.domain.exception.InsufficientStockException;
import com.poc.apistyles.domain.exception.InvalidEntityException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void create_withValidData_shouldCreateProduct() {
        Product product = Product.create("Laptop", BigDecimal.valueOf(999.99), 50, "Electronics");

        assertThat(product.id()).isNotNull();
        assertThat(product.name()).isEqualTo("Laptop");
        assertThat(product.price()).isEqualByComparingTo("999.99");
        assertThat(product.stock()).isEqualTo(50);
        assertThat(product.category()).isEqualTo("Electronics");
    }

    @Test
    void create_withBlankName_shouldThrow() {
        assertThatThrownBy(() -> Product.create("", BigDecimal.TEN, 10, "Test"))
                .isInstanceOf(InvalidEntityException.class)
                .hasMessageContaining("name must not be blank");
    }

    @Test
    void create_withNegativePrice_shouldThrow() {
        assertThatThrownBy(() -> Product.create("Item", BigDecimal.valueOf(-1), 10, "Test"))
                .isInstanceOf(InvalidEntityException.class)
                .hasMessageContaining("price must not be negative");
    }

    @Test
    void create_withNegativeStock_shouldThrow() {
        assertThatThrownBy(() -> Product.create("Item", BigDecimal.TEN, -1, "Test"))
                .isInstanceOf(InvalidEntityException.class)
                .hasMessageContaining("stock must not be negative");
    }

    @Test
    void reserveStock_withSufficientStock_shouldDecrement() {
        Product product = Product.create("Item", BigDecimal.TEN, 100, "Test");
        Product reserved = product.reserveStock(30);

        assertThat(reserved.stock()).isEqualTo(70);
        assertThat(product.stock()).isEqualTo(100);
    }

    @Test
    void reserveStock_withInsufficientStock_shouldThrow() {
        Product product = Product.create("Item", BigDecimal.TEN, 5, "Test");

        assertThatThrownBy(() -> product.reserveStock(10))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("requested 10")
                .hasMessageContaining("available 5");
    }

    @Test
    void releaseStock_shouldIncrement() {
        Product product = Product.create("Item", BigDecimal.TEN, 50, "Test");
        Product released = product.releaseStock(20);

        assertThat(released.stock()).isEqualTo(70);
    }

    @Test
    void hasEnoughStock_shouldReturnCorrectly() {
        Product product = Product.create("Item", BigDecimal.TEN, 10, "Test");

        assertThat(product.hasEnoughStock(10)).isTrue();
        assertThat(product.hasEnoughStock(11)).isFalse();
    }
}
