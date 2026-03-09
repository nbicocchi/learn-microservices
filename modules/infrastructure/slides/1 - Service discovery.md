# Service Discovery

In distributed systems, services must be able to **locate each other’s network address** to communicate.
This process is known as **service discovery**.

* **Horizontal Scaling**

    * New service instances can be **added or removed dynamically** (e.g., containers).
    * Service discovery allows clients to **automatically find available instances**.
    * Workload can be **distributed across multiple instances**.

* **Fault Tolerance**

    * Service instances may **fail or become unavailable**.
    * Discovery systems **monitor service health**.
    * Unhealthy instances are **removed from the registry**, ensuring requests are routed only to healthy services.

## DNS-based service discovery
In the non-cloud world, service location resolution was often solved through a combination of a DNS and a network load balancer.

![](images/traditional-load-balancer.webp)

While this model works well for applications with a relatively small number of services running on a set of static servers, it is less suited for microservice architectures. The limitations include:

* Traditional load balancers are **statically managed**, making them inflexible in dynamic environments.
* The registration of new service instances is **not dynamic**, so adding or removing services requires manual intervention.
* Centralizing services behind a single cluster of load balancers **restricts horizontal scalability**, as all traffic must pass through a single dispatching point.
* Even if the load balancer is made highly available, it remains a **single point of failure for the entire infrastructure**.


Below an example nginx configuration managing a set of three replicas. It's static nature is self-evident.

```text
worker_processes auto;

events {
    worker_connections 1024;
}

http {
    upstream mathservice {
        server math-service-1:8080;
        server math-service-2:8080;
        server math-service-3:8080;
    }

    server {
        listen 8080;

        location /primes {
            proxy_pass http://mathservice;
        }

        location / {
            return 200 'Welcome to the Frontend!\n';
            add_header Content-Type text/plain;
        }
    }
}
```


## Cloud-native service discovery

A robust service discovery mechanism ensures that services dynamically indicate their physical location instead of requiring manual DNS or load balancer configuration.
Key components of cloud-native service discovery include:

* **Service Registration** – When a service instance starts, it registers its network location (IP address and port) with the service discovery system so that other services can locate and communicate with it.

* **Information Sharing** – In distributed discovery systems, each service typically registers with one discovery node. The registration information is then propagated to the other nodes in the cluster, often through a peer-to-peer synchronization mechanism, ensuring that all nodes maintain a consistent view of available services.

* **Health Monitoring** – Service instances periodically report their health status to the discovery system. If an instance becomes unhealthy or stops responding, it is automatically removed from the registry, preventing traffic from being routed to failed services.

![](images/service-discovery.webp)


Cloud native service discovery is:

- **Highly available** – Supports clustering to enable seamless failover if a node becomes unavailable.
- **Load balanced** – Distributes requests evenly across all service instances.
- **Fault-tolerant** – Automatically detects and removes unhealthy service instances without manual intervention.
- **Peer-to-peer** – Shares service health information across nodes, often using gossip-style protocols for efficient data propagation.
- **Resilient** – Caches service information locally, allowing continued operation even if the discovery service becomes unavailable.






## Cloud-native load balancing

Load balancing ensures that requests to a service are distributed evenly among multiple instances to prevent overloading any single instance. It improves both scalability and fault tolerance.

There are two types of load balancing in microservices:
- **Server-side load balancing**: A dedicated load balancer sits between the client and service instances. It handles the distribution of incoming requests to service instances based on various policies.
- **Client-side load balancing**: The client selects an instance from the available ones, often using a round-robin, random, or least-connections strategy. In this case, the client needs to have access to the service registry.

![](images/client-side-vs-server-side-lb.webp)

### Client-Side Load Balancing
In Client-side Load Balancing, **the logic of Load Balancer is part of the client itself**, and it carries the list of services and determines to which service a particular request must be directed based on some algorithm.

![](images/client-side-load-balancing.webp)

* No more single point of failure in Client Side Load Balancer.
* Less network latency as the client can directly call the backend servers.
* Cost Reduction as there is no need for dedicated hardware/software.
* More complex client (discovery code mixed with service code)
* Implementations: **Netflix Eureka**

### Server-Side Load Balancing
If you are keeping the load balancer on the server side, then it’s called **Server-Side Load Balancing**. In Server-side load balancing, the instances of the service are deployed on multiple locations and then a load balancer is placed in front of them. Firstly, all the incoming requests come to the load balancer which acts as a middle component. Then it determines to which server a particular request must be directed based on some algorithm.

* Single point of failure.
* Increased network latency.
* Implementations: **Spring Cloud Gateway, nginx, traefik**


## Resources
- Spring Microservices in Action (Chapter 6)
- Microservices with Spring Boot 3 and Spring Cloud (Chapter 9)

