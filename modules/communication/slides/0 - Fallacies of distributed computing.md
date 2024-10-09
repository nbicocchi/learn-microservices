# Fallacies of distributed computing

### The Network is Reliable
**Description**: This fallacy assumes that network connections are always stable and that there will be no disruptions. In reality, networks can fail due to various reasons like hardware issues, configuration errors, or transient faults.

**Spring Boot Example**:
The example simulates a network call that may fail randomly, illustrating the importance of handling network unreliability with appropriate error handling and retry logic.

```java
@RestController
@RequestMapping("/api")
public class NetworkReliabilityController {
    
    @GetMapping("/reliable")
    public ResponseEntity<String> reliableService() {
        try {
            // Simulate a network call
            String response = callExternalService();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service Unavailable");
        }
    }

    private String callExternalService() throws Exception {
        // Simulate a possible network failure
        if (Math.random() > 0.7) {
            throw new RuntimeException("Network Failure");
        }
        return "Successful Response";
    }
}
```

### Latency is Zero
**Description**: This fallacy assumes that communication between services occurs instantaneously, neglecting the fact that requests and responses can take time due to various factors, including network speed, processing delays, and other overheads.

**Spring Boot Example**:
The example introduces artificial latency to simulate real-world delays in service communication.

```java
@GetMapping("/latency")
public ResponseEntity<String> simulateLatency() {
    try {
        Thread.sleep(2000); // Simulate 2 seconds of latency
        return ResponseEntity.ok("Response after latency");
    } catch (InterruptedException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
    }
}
```

### Bandwidth is Infinite
**Description**: This fallacy assumes that the network can handle any amount of data being sent or received at any time without degradation in performance. In reality, bandwidth is limited, and large data transfers can lead to bottlenecks.

**Spring Boot Example**:
The example demonstrates a file upload endpoint that checks the size of the uploaded file to prevent exceeding a predefined limit, thus illustrating bandwidth constraints.

```java
@PostMapping("/upload")
public ResponseEntity<String> uploadFile(@RequestBody byte[] fileData) {
    if (fileData.length > 10_000_000) { // Limit payload to 10MB
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File too large");
    }
    return ResponseEntity.ok("File uploaded successfully");
}
```

### The Network is Secure
**Description**: This fallacy assumes that all communications over the network are inherently secure. In reality, security must be explicitly designed into network communications to protect against threats like eavesdropping, tampering, and unauthorized access.

**Spring Boot Example**:
The example includes a simple security check to simulate a situation where a connection may not be secure, highlighting the need for security measures in network communications.

```java
@GetMapping("/secure")
public ResponseEntity<String> secureEndpoint() {
    // Simulate a check for secure connection
    if (!isConnectionSecure()) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Connection is not secure");
    }
    return ResponseEntity.ok("Secure Connection");
}

private boolean isConnectionSecure() {
    // Simple check (placeholder for real security check)
    return false; // Simulating insecure connection
}
```

### Topology Doesn't Change
**Description**: This fallacy assumes that the network structure remains constant over time. In reality, distributed systems often experience changes due to scaling, failure, or reconfiguration, which can impact service discovery and availability.

**Spring Boot Example**:
The example shows a service discovery mechanism where the available services change dynamically, illustrating the need to account for topology changes in distributed systems.

```java
@Service
public class DiscoveryService {
    private List<String> services = new ArrayList<>();

    public void registerService(String serviceUrl) {
        services.add(serviceUrl);
    }

    public List<String> getAvailableServices() {
        // Simulate change in topology
        return services.stream().filter(service -> Math.random() > 0.5).collect(Collectors.toList());
    }
}
```

### There is One Administrator
**Description**: This fallacy assumes a single authority manages and controls all aspects of the distributed system. However, in reality, distributed systems may have multiple administrators, each responsible for different components or services.

**Spring Boot Example**:
The example shows a multi-tenant administration scenario where settings can be retrieved for different tenants, emphasizing the need for decentralized management.

```java
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @GetMapping("/settings")
    public ResponseEntity<String> getAdminSettings(@RequestParam String tenantId) {
        // Simulate fetching settings for multiple tenants
        return ResponseEntity.ok("Settings for tenant " + tenantId);
    }
}
```

### Transport Cost is Zero
**Description**: This fallacy assumes that transferring data across the network incurs no cost, neglecting the reality that data transfer can lead to increased latency, operational costs, and resource usage, especially when dealing with large volumes of data.

**Spring Boot Example**:
The example simulates a cost calculation for data transfer, warning if the cost exceeds a certain threshold, illustrating the need to consider transport costs.

```java
@PostMapping("/transfer")
public ResponseEntity<String> transferData(@RequestBody String data) {
    // Simulate cost calculation
    double transferCost = data.length() * 0.01; // Simulated cost per byte
    if (transferCost > 100) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body("Transfer exceeds budget");
    }
    return ResponseEntity.ok("Data transferred successfully");
}
```

### The Network is Homogeneous
**Description**: This fallacy assumes that all components in the network are similar, ignoring the reality that different services may be implemented in various programming languages, run on different platforms, or operate under different configurations.

**Spring Boot Example**:
The example demonstrates how an endpoint can handle different types of payloads (e.g., Map and String), highlighting the need for compatibility in heterogeneous environments.

```java
@PostMapping("/process")
public ResponseEntity<String> processRequest(@RequestBody Object payload) {
    if (payload instanceof Map) {
        return ResponseEntity.ok("Processing Map format");
    } else if (payload instanceof String) {
        return ResponseEntity.ok("Processing String format");
    }
    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Unsupported format");
}
```

## Resources
- [Fallacies of Distributed Systems](https://www.youtube.com/watch?v=8fRzZtJ_SLk&list=PL1DZqeVwRLnD3EjyciYAO82dT9Owiq8I5)

