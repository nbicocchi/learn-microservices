# Distributed caching

## Implementing distributed caching

Setting up a distributed cache involves several steps, from choosing the right caching solution to configuring and deploying it in a distributed environment. Here's a general step-by-step guide:

* Select a suitable distributed caching solution based on application requirements and infrastructure.
* Install and configure the caching software on each node or server in the distributed system.
* Define data partitioning and replication strategies to ensure efficient data distribution and high availability.
* Integrate the caching solution with the application, ensuring that data reads and writes are directed to the cache.
* Monitor and fine-tune the cache performance, adjusting configurations as needed for optimal results.

## Cache Abstraction

At its essence, **the cache abstraction applies caching to Java methods, effectively reducing the number of executions based on cached information**. Each time a designated method is invoked, the abstraction applies a caching behavior that checks whether the method has been previously invoked for any given argument:

* if it has been invoked (**cache hit**), the cached result is retrieved and returned without having to execute the method again.
* if the method hasn't been previously invoked (**cache miss**), then the method is called, and the result is cached and returned to the user.

With this approach, expensive methods (whether CPU-intensive or IO-intensive) can be invoked only the first time for a given set of parameters and the result reused without having to actually invoke the method again.

![](images/cache-spring-boot-redis.avif)

**The caching logic is applied transparently without any interference to the invoker**. In fact, the method's invoker does not need to be aware of or explicitly handle caching mechanisms. Instead, the caching abstraction handles these operations behind the scenes, improving performance and reducing unnecessary computational load without requiring additional effort from the developer.

**As with other services in the Spring Framework (e.g., Spring Cloud Stream), the caching service is an abstraction (not a cache implementation) and requires the use of actual storage to store the cache data.** That is, the abstraction frees you from having to write the caching logic but does not provide the actual data store. This abstraction is materialized by the **`org.springframework.cache.Cache`** and **`org.springframework.cache.CacheManager`** interfaces.

**The caching abstraction provides other cache-related operations, such as the ability to update the content of the cache or to remove one or all entries**. These are useful if the cache deals with data that can change during the course of the application.

## Redis
Redis is an open source and fast key-value data storage and processing system. Redis, abbreviated as “Remote Dictionary Server”, is a NoSql database that generally runs in memory. Key Features and Strengths:

* **Speed**: Redis runs in memory for fast access and can process data faster. This allows data to be read and written quickly, which helps applications to be high-performing.
* **Key-Value Database**: Redis uses the map data structure. Each piece of data (value) is associated with a unique key. In other words, all information held is in the form of key-value pairs.
* **Data Types**: Redis supports not only simple text but also complex data types. Well; It also allows us to use data types such as list, set, map.
* **High Accessibility**: Redis supports features such as server replication and regional load balancing (sharding) to provide high availability. This ensures that our applications run uninterrupted.
* **Transaction Support**: Redis offers transaction support. It allows us to group multiple commands into a process and execute that process atomically.

Redis can be used in various ways, including:
1. **In-Memory Database:** Handling a large amount of real-time data is a common challenge. A real-time database is a type of data repository designed to capture, analyze, and increase an incoming stream of data.
2. **Cache:** Many applications struggle with the need to store and retrieve data quickly, especially in systems with high latency. Due to its speed, Redis is the ideal choice for caching API calls, session states, complex computations, and database queries.
3. **Message Broker (MQ):** It has always been difficult to stream data around the organization and make it accessible for various system components. Redis supports messaging functionalities, serving as a message broker for facilitating communication between different system components.




## About the project

This project is developed using a small set of cooperating microservices, composed of three core services: eureka, a gateway, and a _product service_. The _product service_ stores its data in a mongodb database, exposes them through a REST API, and interacts with a Redis instance for caching. More specifically, it describes each product with the following attributes:

```
- Product ID
- Name
- Weight
```

## Maven dependencies

Add the **spring-boot-starter-cache** and **spring-boot-starter-data-redis** dependencies to the product composite service
  _pom.xml_ file.
```
	<dependencies>
	...
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-cache</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-redis</artifactId>
		</dependency>
	...
	</dependencies>
```

## Docker configuration

The following _docker-compose.yml_ enables the needed services.

```
services:
  eureka:
    build: eureka-server
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  gateway:
    build: gateway-service-end
    mem_limit: 512m
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  product:
    build: product-service-end
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  mongodb:
    image: mongo:latest
    mem_limit: 512m
    ports:
      - "27017:27017"
    command: mongod
    healthcheck:
      test: "mongostat -n 1"
      interval: 5s
      timeout: 2s
      retries: 60

  redis:
    image: redis:latest
    mem_limit: 512m
    ports:
      - "6379:6379"
    healthcheck:
      test: [ "CMD", "redis-cli", "ping" ]
      interval: 5s
      timeout: 2s
      retries: 60

```

## Service configuration
* Update the product composite service _application.yml_ file to configure the Spring Boot application to connect to both MongoDB and Redis.

```
server.port: 7000
server.error.include-message: always

spring:
  cache:
    type: redis
  data:
    redis:
      host: localhost
      port: 6379
    mongodb:
      host: localhost
      port: 27017
      database: products
      auto-index-creation: true
      
cache:
  config:
    entryTtl: 60

logging:
  level:
    root: INFO
    com.baeldung: DEBUG
    org.springframework.cache: TRACE
    org.springframework.data.mongodb.core.MongoTemplate: DEBUG

---
spring.config.activate.on-profile: docker
server.port: 8080
spring.data.redis.host: redis
spring.data.mongodb.host: mongodb
```

Important parts of the preceding code:

* We set the **type** parameter to **redis**, meaning that our application will automatically make the necessary configurations to use Redis as the cache provider. If we had set the **type** parameter to **none**, the application would not configure any cache mechanism and cache operations would be disabled.
* Setting the log level for **org.springframework.cache** to **TRACE** will allow us to see which cache statements are executed in the log.
* When running without Docker using the default Spring profile, the Redis database is expected to be reachable on **localhost:6379**. Instead, When running inside Docker, the Redis database is expected to be reachable on **redis:6379**.

## Service code

### Redis Configuration
To enable Spring Cache in the product microservice, we have to add some configuration. To do that, we create a class named _RedisCacheConfig_ under the _config_ folder so that the necessary Redis settings can be loaded by Spring.

```
@EnableCaching
@Configuration
public class RedisCacheConfig {
    @Value("${cache.config.entryTtl:60}")
    private int entryTtl;

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(entryTtl))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfiguration)
                .transactionAware()
                .build();
    }
}
```

Now let's examine the above class in detail:

* `@EnableCaching`: This annotation is used to enable caching support. It triggers a post-processor that inspects every Spring Bean for the presence of caching annotations on public methods.
* The configuration of Redis Cache created by RedisCacheManager is defined with `RedisCacheConfiguration`. This specifies the default configuration for all caches, including default TTL, that is key expiration time, and serialization settings for converting to and from the binary storage format. It also disables caching of null values.
* `@Value`: This annotation is used to inject values specified in application.yml file. In this example, if entryTtl not defined in application.yml, 60 is assigned by default. This variable represents the time-to-live (TTL) value for the cache. **TTL defines on how long the cached data will be considered valid before it expires and is removed from the cache**.

### @Cacheable and @CacheEvict annotations

You can use `@Cacheable` to demarcate methods that are cacheable - that is, methods for which the result is stored in the cache so that, on subsequent invocations (with the same arguments), the value in the cache is returned without having to actually invoke the method again.

```
@Cacheable(cacheNames = "products", key = "#productId")
public ProductAggregateDto getProduct(int productId) {...}
```

The `cacheNames` attribute establishes a cache with a specific name, while the `key` attribute permits the use of Spring Expression Language to compute the key dynamically. Consequently, the method result is stored in the 'products' cache, where _productId_ serves as the unique key. This approach optimizes caching by associating each result with a distinct key.

The cache abstraction allows not just population of a cache store but also eviction. This process is useful for removing stale or unused data from the cache. `@CacheEvict` annotation demarcates methods that perform cache eviction, that is methods that act as triggers for removing data from the cache. As for `@Cacheable` annotation, we can use `cacheNames` and `key` attributes to remove a specific data from the cache specified.

```
@CacheEvict(cacheNames = "products", key = "#productId")
public void deleteProduct(int productId) {...}
```

## Trying out Redis Cache

Once everything is set up, we can start the system landscape with the following commands:

```
$ mvn clean package -Dmaven.test.skip=true
$ docker-compose build
$ docker-compose up -d
```

Access products with IDs 1 and 2.

```
$ curl -X GET http://localhost:8080/product/1 | jq
{
  "productId": 1,
  "name": "p-id1",
  "weight": 1
}

$ curl -X GET http://localhost:8080/product/2 | jq
{
  "productId": 2,
  "name": "p-id2",
  "weight": 2
}
```

The product service logs will contain lines similar to the following stating that valid cache entries are not available. Thus, mongoDB is queried and cache entries are created.

```
2024-06-05 16:46:31 2024-06-05T14:46:31.321Z TRACE 1 --- [product-service] [ntLoopGroup-4-3] o.s.cache.interceptor.CacheInterceptor   : Creating cache entry for key '1' in cache(s) [products]

2024-06-05 16:46:31 2024-06-05T14:46:31.321Z TRACE 1 --- [product-service] [ntLoopGroup-4-3] o.s.cache.interceptor.CacheInterceptor   : Creating cache entry for key '2' in cache(s) [products]
```

Repeating the two above queries again, will produce different logs as show below.

```
2024-06-05 16:44:35 2024-06-05T14:44:35.849Z TRACE 1 --- [product-service] [or-http-epoll-2] o.s.cache.interceptor.CacheInterceptor   : Cache entry for key '1' found in cache(s) [products]

2024-06-05 16:44:35 2024-06-05T14:44:35.849Z TRACE 1 --- [product-service] [or-http-epoll-2] o.s.cache.interceptor.CacheInterceptor   : Cache entry for key '2' found in cache(s) [products]
```

# Redis Multi-node deployment: Replication vs. Cluster vs. Sentinels

There are two ways of using Redis:

* **Single node deployment** where all the reads and writes happens from single machine
* **Multi node deployment** where reads and writes can happen from multiple machine

It’s clear that single node deployment has three major problems:

* **Scalability (Data size)**: When you have only one machine, it means your data should fit in that single machine
* **Scalability (TPS)**: One machine has limited resource in terms of CPU, so it can support a limited number of “transaction per seconds” (TPS). If your reads/writes go beyond that limit, your machine/pipeline can fail
* **Availability**: When you are running everything on single machine, if that machine crashes, you will loose your Redis database

When you face any of the above 3 problems, you have only one option — use multi-node Redis deployment. The challenge is, there are 3 forms of Redis multi-node deployment and the choice depends on the problem that you are facing. The three multi-node deployment options are:

* Replication
* Cluster
* Sentinels

# Replication

![](https://miro.medium.com/v2/resize:fit:1122/1*DphpQeKEVIQVCgxU2_vI_g.png)

The diagram above shows a typical setup of master-replica multi-node deployment.

On the left we have master node — which handles all the writes and some reads.
On the right are 3 replica nodes — which handles reads and no writes.
Data that is written to the master node is sent to all the replicas in real-time so that all replicas have up-to-date copies of master data and can support read requests.

* All the writes are still handled by single server — so there is no improvement in writes/second
* Data is not divided among the nodes, instead all the data from master node is maintained as a copy in replica nodes — so there is no improvement in amount of data that can be stored
* If your master node goes down, all write requests will fail and replica node will continue to serve read requests (non-latest data, as new writes fail and will not go to replica nodes) — so your availability does not improve
* What improves is number of reads that your database can handle. So if your database was originally able to handle 1000 reads/second from single machine, since you have 4 machines (1 master + 3 replicas) — now you can handle 4000 reads/second (approximately)

# Cluster

![](https://miro.medium.com/v2/resize:fit:1400/1*iOMCYDrYkXUNNuK-LlrDcA.png)

There are multiple master nodes (3 in this case) and each master can have one or more multiple replicas (3 in this case).

* Data is divided among the master nodes. So if you have 100GB of data; 30GB might be stored in Master1, 40GB in Master2 and 30GB in Master3 — so we have data storage scalability
* Since masters are solely responsible to process write requests and we have more than 1 master in the cluster — we have write/second scalability
* If a master node goes down, one of its replica node will be promoted as master — so there is increase in availability of your database even when there are node failures (these kind of systems are called highly-available systems)


# Sentinels

Redis sentinels are multi-node deployment where you can have high availability even with just one master node

We saw in the cluster mode, that high availability is achieved when during master node failures, one of the replica node is promoted as master. But this high-availability is achieved because we have multiple masters which are configured to talk among themselves (gossip protocol) and keep a watch on each other, do the failover (process of promoting replica as master) when a master has gone down.

But, maintaining multiple masters has its challenges too. Although going in details about them is out of scope for this discussion, but one challenge is — multi-key operation. Say, you perform a multi-key operation (like fetch two keys, update 2 keys, etc in single command), both keys should be in the same master; if not, the command will fail.

So, in cases where you don’t really need multiple masters (because your dataset is not large enough that it has to be distributed among multiple machines, or your write requests are small enough to be handled by one machine) but you do need a highly-available system — that is, when master fails, you still need you database to be up: that’s when you can use Redis Sentinels

![](https://miro.medium.com/v2/resize:fit:1200/1*ejl4ZXCUXd57rncaGRufpg.png)

This is what a typical Sentinel deployment looks like. You have your regular redis deployment with one master and multiple replica, along with it you have Sentinel service running on different machines that is constantly monitoring the master node and talking among each other.

The moment one of the sentinel node is not able to reach master node, it will start the failover process by asking other sentinels to check if they also see the master as unreachable (generally a majority vote is required to proof that master is really not reachable).

Once the majority is reached, one of the sentinel will becomes a leader to do the failover process wherein it will appoint one replica as master and direct rest of the replicas to start using the new master.

## Resources
* [Microservices with Spring Boot 3 and Spring Cloud - Third Edition](https://www.packtpub.com/product/microservices-with-spring-boot-3-and-spring-cloud-third-edition-third-edition/9781805128694)
* [Cache Abstraction](https://docs.spring.io/spring-framework/reference/integration/cache.html)
* [Spring Redis Cache](https://docs.spring.io/spring-data/redis/reference/redis/redis-cache.html)
* https://positivethinking.tech/insights/distributed-caching-using-redis-in-spring-boot-applications/

