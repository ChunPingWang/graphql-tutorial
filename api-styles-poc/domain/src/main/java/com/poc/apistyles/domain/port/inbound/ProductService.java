package com.poc.apistyles.domain.port.inbound;

import com.poc.apistyles.domain.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ProductService {
    Product createProduct(String name, BigDecimal price, int stock, String category);
    Product getProduct(UUID id);
    List<Product> getAllProducts();
    Product updateStock(UUID id, int quantity);
    void importProducts(List<Product> products);
}
