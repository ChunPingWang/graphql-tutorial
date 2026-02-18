package com.poc.apistyles.application;

import com.poc.apistyles.domain.exception.EntityNotFoundException;
import com.poc.apistyles.domain.model.Product;
import com.poc.apistyles.domain.port.outbound.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository);
    }

    @Test
    void createProduct_shouldSaveAndReturn() {
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.createProduct("Laptop", BigDecimal.valueOf(999.99), 50, "Electronics");

        assertThat(result.name()).isEqualTo("Laptop");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void getProduct_whenExists_shouldReturn() {
        UUID id = UUID.randomUUID();
        Product product = Product.of(id, "P", BigDecimal.TEN, 10, "C", Instant.now(), Instant.now());
        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        assertThat(productService.getProduct(id).id()).isEqualTo(id);
    }

    @Test
    void getProduct_whenNotExists_shouldThrow() {
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void updateStock_shouldUpdateAndSave() {
        UUID id = UUID.randomUUID();
        Product product = Product.of(id, "P", BigDecimal.TEN, 10, "C", Instant.now(), Instant.now());
        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.updateStock(id, 50);

        assertThat(result.stock()).isEqualTo(50);
    }

    @Test
    void importProducts_shouldDelegateToRepository() {
        List<Product> products = List.of(
                Product.create("A", BigDecimal.ONE, 1, "C")
        );
        productService.importProducts(products);
        verify(productRepository).saveAll(products);
    }
}
