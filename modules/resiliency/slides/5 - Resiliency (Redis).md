# Resiliency (Redis)

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


## Maven dependencies

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

```
services:
  redis:
    image: redis:latest
    ports:
      - "6379:6379"
    healthcheck:
      test: [ "CMD", "redis-cli", "ping" ]
      interval: 10s
      timeout: 5s
      retries: 5
```

## Service configuration

```
spring:
  cache:
    type: redis
  data:
    redis:
      host: localhost
      port: 6379
      
cache:
  config:
    entryTTL: 60

logging:
  level:
    org.springframework.cache: TRACE

---
spring.config.activate.on-profile: docker
spring.data.redis.host: redis
```

* We set the **type** parameter to **redis**, meaning that our application will automatically make the necessary configurations to use Redis as the cache provider. If we had set the **type** parameter to **none**, the application would not configure any cache mechanism and cache operations would be disabled.
* Setting the log level for **org.springframework.cache** to **TRACE** will allow us to see which cache statements are executed in the log.
* When running without Docker using the default Spring profile, the Redis database is expected to be reachable on **localhost:6379**. Instead, When running inside Docker, the Redis database is expected to be reachable on **redis:6379**.

## Service code

### Redis Configuration
To enable Spring Cache in the product microservice, we have to add some configuration. To do that, we create a class named _RedisCacheConfig_ under the _config_ folder so that the necessary Redis settings can be loaded by Spring.

```java
@EnableCaching
@Configuration
public class RedisCacheConfig {
    @Value("${cache.config.entryTTL:60}")
    private int entryTTL;

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(entryTTL))
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
* `@Value`: This annotation is used to inject values specified in application.yml file. In this example, if entryTTL not defined in application.yml, 60 is assigned by default. This variable represents the time-to-live (TTL) value for the cache. **TTL defines on how long the cached data will be considered valid before it expires and is removed from the cache**.

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


## Resources
