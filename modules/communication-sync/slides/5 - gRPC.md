# gRPC with Protocol Buffers

Refer to the *product-service-grpc* example (labs/product-service-grpc):

The **product-service** manages product data, exposing it through a **gRPC interface** using **Protocol Buffers (Protobuf)** for serialization.

---

## Model Class

The underlying model remains identical to the GraphQL version:

```java
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
@Data
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull @EqualsAndHashCode.Include private String uuid;
    @NonNull private String name;
    @NonNull private Double weight;
}
```

---

## Dependencies

To enable **gRPC** in a **Spring Boot** project, include the following dependencies in your `pom.xml`:

```xml
<dependency>
    <groupId>javax.annotation</groupId>
    <artifactId>javax.annotation-api</artifactId>
    <version>1.3.2</version>
</dependency>

<dependency>
    <groupId>net.devh</groupId>
    <artifactId>grpc-server-spring-boot-starter</artifactId>
    <version>3.0.0.RELEASE</version>
</dependency>
```

You also need the **Protobuf compiler plugin** to generate Java sources from `.proto` files:

```xml
<build>
    <extensions>
        <extension>
            <groupId>kr.motd.maven</groupId>
            <artifactId>os-maven-plugin</artifactId>
            <version>1.7.0</version>
        </extension>
    </extensions>

    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>

        <plugin>
            <groupId>org.xolstice.maven.plugins</groupId>
            <artifactId>protobuf-maven-plugin</artifactId>
            <version>0.6.1</version>
            <configuration>
                <protocArtifact>com.google.protobuf:protoc:3.21.12:exe:${os.detected.classifier}</protocArtifact>
                <pluginId>grpc-java</pluginId>
                <pluginArtifact>io.grpc:protoc-gen-grpc-java:1.59.0:exe:${os.detected.classifier}</pluginArtifact>
                <protoPath>src/main/proto</protoPath>
                <includeStdTypes>true</includeStdTypes>
            </configuration>
            <executions>
                <execution>
                    <goals>
                        <goal>compile</goal>          <!-- generates protobuf messages -->
                        <goal>compile-custom</goal>   <!-- generates gRPC stubs -->
                    </goals>
                </execution>
            </executions>
        </plugin>

    </plugins>
</build>
```

---

## Protobuf Schema

The **.proto** file defines the **API contract** (types, messages, and services).
It serves as both documentation and code-generation source for clients and servers.

```protobuf
syntax = "proto3";

import "google/protobuf/empty.proto";

option java_multiple_files = true;
option java_package = "com.example.product";
option java_outer_classname = "ProductProto";

message Product {
  string uuid = 1;
  string name = 2;
  double weight = 3;
}

message ProductRequest {
  string uuid = 1;
}

message ProductList {
  repeated Product products = 1;
}

message CreateProductRequest {
  Product product = 1;
}

message UpdateProductRequest {
  string uuid = 1;
  Product product = 2;
}

message DeleteProductRequest {
  string uuid = 1;
}

message DeleteProductResponse {
  bool success = 1;
}

service ProductService {
  rpc GetAllProducts(google.protobuf.Empty) returns (ProductList);
  rpc GetProductByUuid(ProductRequest) returns (Product);
  rpc CreateProduct(CreateProductRequest) returns (Product);
  rpc UpdateProduct(UpdateProductRequest) returns (Product);
  rpc DeleteProduct(DeleteProductRequest) returns (DeleteProductResponse);
}
```

---

## gRPC Service Implementation

After compiling the `.proto` file, Java stubs are generated in `target/generated-sources/protobuf`.
The server implements these stubs by extending the base class `ProductServiceGrpc.ProductServiceImplBase`.

```java
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

```

## Resources

* [Spring Boot gRPC Starter](https://yidongnan.github.io/grpc-spring-boot-starter/en/server/getting-started.html)
* [Protocol Buffers Reference](https://developers.google.com/protocol-buffers)
* [gRPC Official Website](https://grpc.io/)

