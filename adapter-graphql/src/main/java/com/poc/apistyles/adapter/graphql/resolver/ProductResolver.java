package com.poc.apistyles.adapter.graphql.resolver;

import com.poc.apistyles.domain.model.Product;
import com.poc.apistyles.domain.port.inbound.ProductService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class ProductResolver {

    private final ProductService productService;

    public ProductResolver(ProductService productService) {
        this.productService = productService;
    }

    @QueryMapping
    public Product product(@Argument UUID id) {
        return productService.getProduct(id);
    }

    @QueryMapping
    public List<Product> products() {
        return productService.getAllProducts();
    }
}
