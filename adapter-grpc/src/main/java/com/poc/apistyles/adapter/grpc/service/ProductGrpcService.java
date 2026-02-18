package com.poc.apistyles.adapter.grpc.service;

import com.poc.apistyles.adapter.grpc.protobuf.*;
import com.poc.apistyles.domain.model.Product;
import com.poc.apistyles.domain.port.inbound.ProductService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@GrpcService
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService productService;

    public ProductGrpcService(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void getProduct(GetProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        UUID id = UUID.fromString(request.getId());
        Product product = productService.getProduct(id);
        responseObserver.onNext(toResponse(product));
        responseObserver.onCompleted();
    }

    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        Product product = productService.createProduct(
                request.getName(),
                BigDecimal.valueOf(request.getPrice()),
                request.getStock(),
                request.getCategory()
        );
        responseObserver.onNext(toResponse(product));
        responseObserver.onCompleted();
    }

    @Override
    public void listProducts(ListProductsRequest request, StreamObserver<ListProductsResponse> responseObserver) {
        List<Product> products = productService.getAllProducts();
        List<ProductResponse> responses = products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        responseObserver.onNext(ListProductsResponse.newBuilder()
                .addAllProducts(responses)
                .setTotal(responses.size())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateStock(UpdateStockRequest request, StreamObserver<ProductResponse> responseObserver) {
        UUID id = UUID.fromString(request.getId());
        Product product = productService.updateStock(id, request.getQuantity());
        responseObserver.onNext(toResponse(product));
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ImportProductsRequest> importProducts(StreamObserver<ImportProductsResponse> responseObserver) {
        return new StreamObserver<ImportProductsRequest>() {
            private final java.util.ArrayList<Product> products = new java.util.ArrayList<>();

            @Override
            public void onNext(ImportProductsRequest request) {
                for (var productReq : request.getProductsList()) {
                    Product product = productService.createProduct(
                            productReq.getName(),
                            BigDecimal.valueOf(productReq.getPrice()),
                            productReq.getStock(),
                            productReq.getCategory()
                    );
                    products.add(product);
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                List<ProductResponse> responses = products.stream()
                        .map(ProductGrpcService.this::toResponse)
                        .collect(Collectors.toList());
                responseObserver.onNext(ImportProductsResponse.newBuilder()
                        .setImported(products.size())
                        .addAllProducts(responses)
                        .build());
                responseObserver.onCompleted();
            }
        };
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.newBuilder()
                .setId(product.id().toString())
                .setName(product.name())
                .setPrice(product.price().doubleValue())
                .setStock(product.stock())
                .setCategory(product.category())
                .setCreatedAt(product.createdAt().toString())
                .setUpdatedAt(product.updatedAt().toString())
                .build();
    }
}
