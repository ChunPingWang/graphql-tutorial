package com.poc.apistyles.adapter.rest.controller;

import com.poc.apistyles.adapter.rest.dto.ProductResponse;
import com.poc.apistyles.domain.model.Product;
import com.poc.apistyles.domain.port.inbound.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts().stream()
            .map(ProductResponse::from)
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID id) {
        Product p = productService.getProduct(id);
        return ResponseEntity.ok(ProductResponse.from(p));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam int stock,
            @RequestParam String category) {
        Product p = productService.createProduct(name, price, stock, category);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(p));
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> updateStock(@PathVariable UUID id, @RequestParam int quantity) {
        Product p = productService.updateStock(id, quantity);
        return ResponseEntity.ok(ProductResponse.from(p));
    }
}
