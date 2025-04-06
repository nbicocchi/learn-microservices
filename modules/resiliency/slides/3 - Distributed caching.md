# Distributed caching

![](images/cache-architecture.webp)

By caching data, microservices can reduce the need for repeated, expensive operations, such as retrieving data from a database or performing complex computations. Instead, the cached results can be directly served, significantly improving response times and overall system performance.
* **Caching improves resiliency because services can continue to serve requests even if their backend systems are temporarily unavailable**.
* **Caching improves scalability by offloading the workload from backend systems.**



## Cache-Aside (Lazy Loading)

![](https://codeahoy.com/img/cache-aside.png)

- Read Flow:  
  → Application checks the cache.  
  → On cache miss: application reads from the database, stores the result in cache, and returns it.
- Write Flow:  
  → Application writes directly to the database.  
  → Optionally, it also updates or invalidates the cache (TTL).

> Very flexible and widely used. Suitable when reads are much more frequent than writes.

> When the application wants full control over cache population. Good for read-heavy workloads with occasional writes.

## Read-Through Cache

![](https://codeahoy.com/img/read-through.png)

- Read Flow:  
  → Application queries the cache.  
  → On cache miss: the cache itself fetches the data from the database, stores it, and returns it.
- Write Flow:  
  → Application writes directly to the database. Cache is not updated automatically.

> Easier for developers because the cache handles loading logic. Requires the cache system to know how to fetch from DB. Cache, if not invalidated, becomes a "shadow database" that might never update.

> When you want to simplify read logic and avoid code duplication for cache handling.

## Write-Through Cache

![](https://codeahoy.com/img/write-through.png)

- Read Flow:  
  → Application queries the cache.  
  → On cache miss: the cache itself fetches the data from the database, stores it, and returns it.
- Write Flow:  
  → Application writes data to the cache.  
  → The cache synchronously writes it to the database.

> Ensures cache and database are always consistent. But writes can be slower due to double-write.

> When reads and writes happen together often, and consistency is critical.

## Write-Back (Write-Behind) Cache

![](https://codeahoy.com/img/write-back.png)

- Read Flow:  
  → Usually like read-through.
- Write Flow:  
  → Application writes data to the cache.  
  → Cache writes asynchronously to the database (with a delay or batching).

> Very fast writes from the application's perspective, but introduces risk of data loss if cache fails before writing to DB.

> When write performance is more important than immediate consistency (high-throughput systems). Risk of data loss if cache fails before persisting.

## Write-Around Cache

- Read Flow:  
  → Application checks the cache.  
  → On miss: reads directly from DB, but does *not* store result in cache. Only subsequent read requests for the same data trigger caching.
- Write Flow:  
  → Application writes only to the database.

> Avoids polluting the cache with rarely-read data. But frequently-read data may cause repeated cache misses.

> When data is rarely read after being written or for write-heavy workloads where caching writes is not worth it.

## Resources
