# Communication styles (REST)

## Introduction to Microservices Architecture

**REST** is an architectural style that leverages the existing protocols of the web, specifically HTTP. It emphasizes stateless communication and a uniform interface for resource manipulation. The core HTTP methods used in RESTful communication are:

- **GET**: Retrieve information from the server.
- **POST**: Create a new resource on the server.
- **PUT**: Update an existing resource.
- **DELETE**: Remove a resource from the server.

### Key Principles of REST

1. **Statelessness**: Each request from a client to the server contains all the necessary information. The server does not store client context between requests.
2. **Client-Server Separation**: The client and server are independent entities that communicate over the network, allowing for changes on either side without affecting the other.
3. **Resource Identification**: Resources are identified using URIs (Uniform Resource Identifiers), which can be represented in various formats, such as JSON or XML.
4. **Uniform Interface**: RESTful APIs provide a uniform interface, which simplifies and decouples the architecture, making it easier for clients to interact with resources.

## Building RESTful Services with Spring Boot

**Spring Boot** is a powerful framework that simplifies Java application development, including RESTful APIs. It provides various features that facilitate rapid application development, such as auto-configuration and embedded servers.

### Setting Up a Spring Boot Application

To create a RESTful service in Spring Boot, follow these steps:

1. **Create a New Spring Boot Project**:
   Use Spring Initializr to generate a new Spring Boot project with dependencies like `Spring Web`.

2. **Define a Model Class**:
   Create a model class that represents the data structure of the resource. For example, a `User` class:

   ```java
   package com.example.demo.model;

   public class User {
       private Long id;
       private String name;
       private String email;

       // Getters and Setters
   }
   ```

3. **Create a REST Controller**:
   Define a REST controller that handles incoming HTTP requests and responds with the appropriate data.

   ```java
   package com.example.demo.controller;

   import com.example.demo.model.User;
   import org.springframework.web.bind.annotation.*;

   import java.util.ArrayList;
   import java.util.List;

   @RestController
   @RequestMapping("/api/users")
   public class UserController {
       private List<User> users = new ArrayList<>();

       @GetMapping
       public List<User> getUsers() {
           return users;
       }

       @PostMapping
       public User createUser(@RequestBody User user) {
           users.add(user);
           return user;
       }
   }
   ```

### Consuming RESTful Services with RestClient

In a microservices architecture, it is often necessary for services to consume APIs provided by other services. Spring Boot provides a simple way to achieve this through the `RestClient`, which allows for HTTP requests to be made and handled effectively.

#### Configuring RestClient

1. **Add Dependencies**:
   Ensure you have the necessary dependencies for using `RestClient`. If you're using Spring Boot, these dependencies are typically included when you add `Spring Web`.

2. **Create a RestClient Configuration**:
   Define a configuration class to create a `RestClient` bean that can be used throughout your application.

   ```java
   package com.example.demo.config;

   import org.springframework.boot.web.client.RestTemplateBuilder;
   import org.springframework.context.annotation.Bean;
   import org.springframework.context.annotation.Configuration;
   import org.springframework.web.client.RestClient;

   @Configuration
   public class AppConfig {
       @Bean
       public RestClient restClient(RestTemplateBuilder builder) {
           return builder.build();
       }
   }
   ```

#### Using RestClient to Make API Calls

You can use `RestClient` to make HTTP requests to other services. Here's an example of a service that consumes a REST API to fetch user data:

```java
package com.example.demo.service;

import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class UserService {

    private final RestClient restClient;

    @Autowired
    public UserService(RestClient restClient) {
        this.restClient = restClient;
    }

    public User getUserById(Long id) {
        String url = "http://localhost:8080/api/users/" + id;
        return restClient.getForObject(url, User.class);
    }
}
```

### Error Handling with RestClient

When consuming external APIs, it is important to handle errors properly. You can use try-catch blocks to manage exceptions that may occur during API calls.

```
try {
    User user = restClient.getForObject(url, User.class);
} catch (RestClientException e) {
    // Handle the error, e.g., log it or throw a custom exception
}
```

## Resources