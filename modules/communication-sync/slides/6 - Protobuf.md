# **gRPC + Protocol Buffers**

## **What is gRPC?**

gRPC is a **high-performance, cross-language, open-source framework** for **remote procedure calls (RPC)**.
Created by Google, built on:

* **HTTP/2** (multiplexing, header compression, server push)
* **Protocol Buffers (Protobuf)** as the default data format

Ideal for **low latency**, **high throughput**, and **strong contracts**.

---

## **Why gRPC?**

* Faster and more compact than REST/JSON
* Strongly typed interfaces
* Automatic client/server code generation
* Bi-directional streaming
* Native load balancing, deadlines, metadata
* First-class interop (Java, Go, Python, C++, C#, Kotlin...)

---

## **Protocol Buffers (Protobuf)**

A **binary serialization format** with:

* Very compact data representation
* Code generation for strongly typed languages
* Strict backward/forward compatibility rules
* Schema defined in `.proto` files

---

## **Protobuf Schema Example**

```proto
syntax = "proto3";

message User {
  string id = 1;
  int32 age = 2;
  bool is_active = 3;
}
```

Key properties:

* Fields identified by numeric **tags** (compact + stable)
* Supports primitives, enums, nested messages, maps, repeated fields
* Default values (0, false, empty) — no need for `null`

---

## **gRPC Service Definition**

```proto
syntax = "proto3";

service UserService {
  rpc GetUser (UserRequest) returns (UserResponse);
  rpc AddUser (User) returns (UserResponse);
}
```

## **Client/Server Generation**

```
protoc --java_out=. --grpc-java_out=. user.proto
```

Generates:

* Strongly typed client stubs
* Server base classes
* Message classes

---

## **Why Protobuf is Fast**

**Compact Binary Encoding**

* Field names **not included** → only numeric tags
* Variable-length integer encoding (varint)
* Very small payloads (5–10× smaller than JSON)

**Efficient Parsing**

* Precompiled code → no reflection
* Messages mapped to classes
* Zero-copy deserialization patterns available

---

## **Schema Evolution in Protobuf**

**Compatible Changes**

* Adding optional fields
* Renaming a field (if tag number stays the same)
* Removing a field (keep the tag reserved)
* Changing default values (clients unaffected)

**Breaking Changes**

* Changing a field’s type
* Reusing tag numbers
* Changing field labels (`repeated` → non-repeated)
* Removing fields without reserving tags

---

## **gRPC in Java (Spring Boot)**

**Server Implementation**

```java
@GrpcService
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    @Override
    public void getUser(UserRequest req, StreamObserver<UserResponse> res) {
        UserResponse response = ...
        res.onNext(response);
        res.onCompleted();
    }
}
```

**Client Usage**

```java
ManagedChannel channel = ManagedChannelBuilder
    .forAddress("localhost", 50051)
    .usePlaintext()
    .build();

UserServiceGrpc.UserServiceBlockingStub client =
    UserServiceGrpc.newBlockingStub(channel);

UserResponse res = client.getUser(
    UserRequest.newBuilder().setId("123").build()
);
```

---

# **Where gRPC Shines**

## **Best use cases**

* Low-latency internal APIs
* Microservice-to-microservice communication
* High-throughput services
* IoT / robotics / Edge computing
* Real-time streaming
* Strongly typed enterprise systems

## **Not ideal for**

* Public APIs (JSON is easier)
* Browsers (no direct gRPC support → need gRPC-Web)
* Simple CRUD applications

