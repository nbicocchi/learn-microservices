package com.nbicocchi.product.controller;

import com.example.product.ProductList;
import com.example.product.ProductRequest;
import com.example.product.CreateProductRequest;
import com.example.product.UpdateProductRequest;
import com.example.product.DeleteProductRequest;
import com.example.product.DeleteProductResponse;
import com.example.product.ProductServiceGrpc;

import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import com.nbicocchi.product.persistence.model.Product; // JPA entity
import com.nbicocchi.product.service.ProductService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@GrpcService
public class ProductGrpcController extends ProductServiceGrpc.ProductServiceImplBase {

    private final ProductService productService;

    public ProductGrpcController(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void getAllProducts(Empty request, StreamObserver<ProductList> responseObserver) {
        List<Product> products = StreamSupport.stream(productService.findAll().spliterator(), false)
                .collect(Collectors.toList());
        ProductList.Builder builder = ProductList.newBuilder();
        products.forEach(p -> builder.addProducts(
                com.example.product.Product.newBuilder() // fully qualified for gRPC Product
                        .setUuid(p.getUuid())
                        .setName(p.getName())
                        .setWeight(p.getWeight())
                        .build()
        ));

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getProductByUuid(ProductRequest request, StreamObserver<com.example.product.Product> responseObserver) {
        Product p = productService.findByUuid(request.getUuid())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        com.example.product.Product protoProduct = com.example.product.Product.newBuilder()
                .setUuid(p.getUuid())
                .setName(p.getName())
                .setWeight(p.getWeight())
                .build();

        responseObserver.onNext(protoProduct);
        responseObserver.onCompleted();
    }

    @Override
    public void createProduct(CreateProductRequest request, StreamObserver<com.example.product.Product> responseObserver) {
        Product p = new Product(
                request.getProduct().getUuid(),
                request.getProduct().getName(),
                request.getProduct().getWeight()
        );
        Product saved = productService.save(p);

        com.example.product.Product protoProduct = com.example.product.Product.newBuilder()
                .setUuid(saved.getUuid())
                .setName(saved.getName())
                .setWeight(saved.getWeight())
                .build();

        responseObserver.onNext(protoProduct);
        responseObserver.onCompleted();
    }

    @Override
    public void updateProduct(UpdateProductRequest request, StreamObserver<com.example.product.Product> responseObserver) {
        Product p = productService.findByUuid(request.getUuid())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        p.setName(request.getProduct().getName());
        p.setWeight(request.getProduct().getWeight());
        Product updated = productService.save(p);

        com.example.product.Product protoProduct = com.example.product.Product.newBuilder()
                .setUuid(updated.getUuid())
                .setName(updated.getName())
                .setWeight(updated.getWeight())
                .build();

        responseObserver.onNext(protoProduct);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteProduct(DeleteProductRequest request, StreamObserver<DeleteProductResponse> responseObserver) {
        Product p = productService.findByUuid(request.getUuid())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productService.delete(p);

        DeleteProductResponse response = DeleteProductResponse.newBuilder()
                .setSuccess(true)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
