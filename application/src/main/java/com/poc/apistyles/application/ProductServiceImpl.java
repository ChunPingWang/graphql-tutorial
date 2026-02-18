package com.poc.apistyles.application;

import com.poc.apistyles.domain.model.Product;
import com.poc.apistyles.domain.port.inbound.ProductService;
import com.poc.apistyles.domain.port.outbound.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(String name, BigDecimal price, int stock, String category) {
        Product product = Product.create(name, price, stock, category);
        return productRepository.save(product);
    }

    @Override
    public Product getProduct(UUID id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product updateStock(UUID id, int quantity) {
        Product product = getProduct(id);
        Product updated = product.withStock(quantity);
        return productRepository.save(updated);
    }

    @Override
    public void importProducts(List<Product> products) {
        productRepository.saveAll(products);
    }
}
