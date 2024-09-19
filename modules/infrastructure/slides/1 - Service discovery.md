# Service Discovery

In any distributed architecture, we need to find the hostname or IP address of where a machine is located. This concept has been around since the beginning of distributed computing and is known formally as *service discovery*.

Service discovery is critical to microservices for two key reasons:
* **Horizontal scaling:** **Microservice architectures need adjustments in the application architecture, such as adding more instances of a service (i.e., more containers)**. This ability to quickly scale services can move a development team that’s used to building monolithic applications away from vertical scaling to
the more robust approach of horizontal scaling.

* **Resiliency:** Microservice architectures have to be designed to prevent a problem in a single service from cascading up to its consumers. **When a microservice instance becomes unhealthy or unavailable, service discovery engines have to remove that instance from the list of available services**. The damage is thus minimized because the service discovery engine routes consumers around the unavailable service.

You might be wondering why we can’t use known approaches such as DNS or load balancers to facilitate service discovery.


## The problem with DNS-based service discovery
If you have an application that calls resources spread across multiple servers, it needs to find the physical location of those resources. In the non-cloud world, service location resolution was often solved through a combination of a DNS and a network load balancer.

![](images/traditional-load-balancer.avif)

While this type of model works well with applications with a relatively small number of services running on a group of static servers, it doesn't work well for microservice architectures. The reasons for this include the following:
* While the load balancer can be made highly available, it’s a **single point of failure for your entire infrastructure**. 
* Centralizing your services behind a single cluster of load balancers **limits your ability to scale horizontally** (all traffic is routed through a single dispatching point). 
* Most traditional load balancers are statically managed. They aren’t designed for fast registration and deregistration of services. In a traditional load balancer scenario, **the registration of new service instances is not done when a new service instance starts**.


## Cloud-native service discovery

The solution for a cloud-based microservice environment is to use a service discovery mechanism that is:
* **Highly available** Service discovery needs to support a “hot” clustering environment where service lookups can be shared across multiple nodes in a service discovery cluster. If a node becomes unavailable, other nodes in the cluster should be able to take over.
* **Peer-to-peer** Service discovery nodes share service instance health information with each other. If you are interested in gossip-style protocols for information propagation, you can have a look at [The Gossip Protocol](https://www.consul.io/docs/internals/gossip.html) or [SWIM: The scalable membership protocol](https://www.brianstorti.com/swim/) articles.
* **Load balanced** Service discovery needs to load balance requests across all service instances. This ensures that the service invocations are spread across all the service instances.
* **Resilient** The service discovery’s client should cache service information locally. Local caching allows for gradual degradation of the service discovery feature so that if the service discovery service becomes unavailable, applications can still function with local information.
* **Fault tolerant** Service discovery needs to detect when a service instance isn't healthy and remove it from the list of available services without any human intervention.

![](images/service-discovery.avif)

### Key Services Actions
The principal objective of service discovery is to **have an architecture where services indicate where they are physically located instead of having to manually configure a load balancer**. Key steps are:
* **Service registration:** As service instances start, they’ll register their physical location (ip and port) that can be used to access them. 
* **Information sharing:** A service usually only registers with one service discovery service instance. Most service discovery implementations use a peer-to-peer model of data propagation, where the data around each service instance is communicated to all the other nodes in the cluster.
* **Health monitoring:** A service instance pushes to or pulls from its status by the service discovery service. Any services failing to return a good health check are removed from the pool of available service instances. 
* **Client lookup of service address**: There are two ways to load balance clients requests to services. One involving a third component (Server-Side Load Balancer), the other involving only the client and the service (Client-Side Load Balancer). 

### Client-Side Load Balancer
If you are keeping the load balancer on the client side and giving the load balancing responsibility to the client, then it’s called **Client-Side Load Balancing**. In Client-side Load Balancing, the logic of Load Balancer is part of the client itself, and it carries the list of services and determines to which service a particular request must be directed based on some algorithm.

![](images/client-side-load-balancing.avif)

* No more single point of failure in Client Side Load Balancer.
* Less network latency as the client can directly call the backend servers.
* Cost Reduction as there is no need for server-side load balancing.
* Additional complexity because microservice code is combined with the load balancer logic.
* Implementations: **Netflix Eureka, HashiCorp Consul**

### Server-Side Load Balancer
If you are keeping the load balancer on the server side, then it’s called **Server-Side Load Balancing**. In Server-side load balancing, the instances of the service are deployed on multiple locations and then a load balancer is placed in front of them. Firstly, all the incoming requests come to the load balancer which acts as a middle component. Then it determines to which server a particular request must be directed based on some algorithm.

* Single point of failure.
* Network latency rises in Server-Side Load Balancing.
* Higher costs.
* Implementations: **Spring Cloud Gateway, Apache ZooKeeper**

## Implementation choices

[Netflix Eureka](https://github.com/Netflix/eureka)
* Eventual consistency
* Client-side load balancing model
* Do not provide support for Multiple Data Centers
* Built-in health checking mechanisms

[HashiCorp Consul](https://developer.hashicorp.com/consul)

* Strong consistency
* Client-side load balancing model
* Support for Multiple Data Centers
* Built-in health checking mechanisms

[Apache Zookeeper](https://zookeeper.apache.org/)

* Strong consistency
* Server-side load balancing model
* Support for Multiple Data Centers
* Does not integrate built-in health checking mechanisms

## Resources
- Spring Microservices in Action (Chapter 6)
- Microservices with Spring Boot 3 and Spring Cloud (Chapter 9)
