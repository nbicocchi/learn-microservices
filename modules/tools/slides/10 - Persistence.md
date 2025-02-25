# Persistence

## Java Persistence Solutions

Spring Data is a family of projects, all related to data access aspects. The goal of this umbrella project is to offer a familiar and consistent Spring-based model for data access, while providing submodules to cover any specific scenario we might have to deal with.

-   [Spring Data Commons](https://github.com/spring-projects/spring-data-commons) - Core Spring concepts underpinning every Spring Data module.
-   [Spring Data JDBC](https://spring.io/projects/spring-data-jdbc) - Spring Data repository support for JDBC.
-   [Spring Data JPA](https://spring.io/projects/spring-data-jpa) - Spring Data repository support for JPA.
-   [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb) - Spring based, object-document support and repositories for MongoDB.
-   [Spring Data R2DBC](https://spring.io/projects/spring-data-r2dbc) - Spring Data repository support for R2DBC.
-   [Spring Data KeyValue](https://github.com/spring-projects/spring-data-keyvalue) - `Map` based repositories and SPIs to easily build a Spring Data module for key-value stores.
-   [Spring Data LDAP](https://spring.io/projects/spring-data-ldap) - Spring Data repository support for [Spring LDAP](https://github.com/spring-projects/spring-ldap).
-   [Spring Data Redis](https://spring.io/projects/spring-data-redis) - Easy configuration and access to Redis from Spring applications.
-   [Spring Data REST](https://spring.io/projects/spring-data-rest) - Exports Spring Data repositories as hypermedia-driven RESTful resources.
-   [Spring Data for Apache Cassandra](https://spring.io/projects/spring-data-cassandra) - Easy configuration and access to Apache Cassandra or large scale, highly available, data oriented Spring applications.
-   [Spring Data for Apache Geode](https://spring.io/projects/spring-data-geode) - Easy configuration and access to Apache Geode for highly consistent, low latency, data oriented Spring applications.


### Spring Data JDBC

This is the JDBC standard, and it’s also the oldest approach for working with databases in Java. Using JDBC requires us to use SQL statements, which can be both an advantage and a disadvantage.

One of the biggest advantages of Spring Data JDBC is the **improved performance when accessing the database as compared to Spring Data JPA**. This is due to Spring Data JDBC communicating directly to the database. Spring Data JDBC does not contain most of the Spring Data magic when querying the database.

One of the biggest disadvantages when using Spring Data JDBC is the dependency on the database vendor. If we decide to change the database from MySQL to Oracle, we might have to deal with problems that arise from databases having different dialects.

### Spring Data JPA

A higher-level alternative to the JDBC standard is the JPA standard (Jakarta Persistence API). This adds a layer of abstraction over the database, the basis of which is **the object-relational mapping (ORM) approach**.

With JPA, we create a set of classes (entities) that map to the database tables and then interact with these classes instead of the database tables directly.

We have to keep in mind that JPA is only the specification. The reference implementation of JPA is [EclipseLink](https://projects.eclipse.org/projects/ee4j.eclipselink), but the most popular implementation is the [Hibernate](https://hibernate.org/) ORM (Object Relational Mapping) framework.

![](images/diagram-db-abstractions.webp)

With JPA, we’re delegating a lot of functionality to the framework, and whenever that happens, **we lose some control** over what’s finally executed. JPA is a very powerful and useful solution, suitable for most projects, and definitely better than writing a lot of boilerplate manually. There might be edge cases, a few exceptional queries, or persistence procedures where it makes sense to use a different approach. This could mean maybe lower level JPA customizations, or even some JDBC operations.

## Configuration

### Maven (`pom.xml`)

```xml
<dependencies>
    <!-- Spring Boot JPA Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- Database Driver (e.g., PostgreSQL) -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>

    <!-- Optional: H2 Database for In-Memory Testing -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

### Spring Boot (`application.yml`)

Despite being very similar, each database requires specific details.

**PostGreSQL Configuration**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/jdbc_schema
    username: user
    password: secret
  jpa:
    open-in-view: false
    hibernate.ddl-auto: create-drop
    defer-datasource-initialization: true
  sql:
    init.mode: always
```

**H2 Configuration**

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
    username: sa
    password:
  jpa:
    open-in-view: false
    hibernate.ddl-auto: create-drop
    defer-datasource-initialization: true
  sql:
    init.mode: always
  h2:
    console:
      enabled: true
```


## Creating Spring Data JPA Repositories

Spring Data Repositories allow us to **carry out standard database operations without having to write a single line of code**.

The framework provides interfaces that supply methods for querying our database.

* _[CrudRepository](https://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/repository/CrudRepository.html)_ provides CRUD functions
* _[PagingAndSortingRepository](https://docs.spring.io/spring-data/data-commons/docs/current/api/org/springframework/data/repository/PagingAndSortingRepository.html)_ provides methods to do pagination and sorting of records
* _[JpaRepository](https://docs.spring.io/spring-data/jpa/docs/current/api/org/springframework/data/jpa/repository/JpaRepository.html)_ provides JPA-related methods such as flushing the persistence context and deleting records in a batch

To create a JPA repository, we have to **create an interface extending one of the Spring Data Repository interfaces**, which in this case is the _CrudRepository_:

```java
@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

}
```

If we open the _CrudRepository_ implementation, we can see that at the core of these repositories is the _Repository<T, ID>_ interface:

```java
public interface CrudRepository<T, ID> extends Repository<T, ID> {
    // ...
}
```

If we go further and open this _org.springframework.data.repository.Repository_ definition, we’ll see that it’s just a marker interface, as it does not define any actual method:

```java
public interface Repository<T, ID> {
}
```

The **Repository interface takes two type parameters (_<T, ID>_)**, which are also taken when we define a _CrudRepository_ interface:

-   _T represents the entity class_
-   _ID represents the type of the entity Id field_

Let’s have a look under the hood of the [CrudRepository interface](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html). **The beauty of this is that we don’t need to implement these methods; Spring will create a suitable proxy instance for the repository interface we define.**

## Using Spring Data Repositories

We can simply _@Autowire_ our created interface into any component.

To save and find products, we can call the _findAll()_ and _save()_ methods. 

```java
@SpringBootApplication
public class App implements ApplicationRunner {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);
    ProductRepository productRepository;

    public App(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public static void main(final String... args) {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        productRepository.save(new Product("Laptop", 2.2));
        productRepository.save(new Product("Bike", 5.5));
        productRepository.save(new Product("Shirt", 0.2));

        Iterable<Product> products = productRepository.findAll();
        for (Product product : products) {
            LOG.info(product.toString());
        }
    }
}
```

## Custom queries

### Custom Queries Using Method Naming Conventions

Spring Data JPA can create queries based on method names by following certain [conventions](https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html).

```java
public interface ProductRepository extends CrudRepository<Product, Long> {

    // Find products by name
    List<Product> findByName(String name);

    // Find products with a weight greater than the given value
    List<Product> findByWeightGreaterThan(Double weight);

    // Find products by name and weight
    List<Product> findByNameAndWeight(String name, Double weight);

    // Find products where the name contains a given substring
    List<Product> findByNameContaining(String nameFragment);

    // Find products with weight between two values
    List<Product> findByWeightBetween(Double minWeight, Double maxWeight);

    // Find all products, ordered by name ascending
    List<Product> findAllByOrderByNameAsc();
}
```

- **`findByName(String name)`**: Finds all products that have a specific `name`.
- **`findByWeightGreaterThan(Double weight)`**: Finds products with a `weight` greater than the specified value.
- **`findByNameAndWeight(String name, Double weight)`**: Finds products with the specified `name` and `weight`.
- **`findByNameContaining(String nameFragment)`**: Finds products where the `name` contains a certain substring (useful for search functionality).
- **`findByWeightBetween(Double minWeight, Double maxWeight)`**: Finds products whose `weight` is within a given range.
- **`findAllByOrderByNameAsc()`**: Finds all products and orders them alphabetically by `name` in ascending order.

### Custom Queries Using `@Query` Annotation

For more complex queries or fine-tuning, you can use JPQL or native SQL with the `@Query` annotation.

```java
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {

    // JPQL Query to find products by name
    @Query("SELECT p FROM Product p WHERE p.name = :name")
    List<Product> findProductsByName(@Param("name") String name);

    // JPQL Query to find products by weight greater than a specific value
    @Query("SELECT p FROM Product p WHERE p.weight > :weight")
    List<Product> findProductsWithWeightGreaterThan(@Param("weight") Double weight);

    // JPQL Query to find products by a name fragment
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:nameFragment%")
    List<Product> findProductsByNameContaining(@Param("nameFragment") String nameFragment);

    // JPQL Query to find products with weight between two values
    @Query("SELECT p FROM Product p WHERE p.weight BETWEEN :minWeight AND :maxWeight")
    List<Product> findProductsByWeightRange(@Param("minWeight") Double minWeight, @Param("maxWeight") Double maxWeight);

    // Native SQL query to find all products ordered by weight
    @Query(value = "SELECT * FROM product ORDER BY weight DESC", nativeQuery = true)
    List<Product> findAllProductsOrderedByWeightDesc();
}
```

- **`findProductsByName(@Param("name") String name)`**: Uses JPQL to find all products with the specified `name`.
- **`findProductsWithWeightGreaterThan(@Param("weight") Double weight)`**: Uses JPQL to find all products with `weight` greater than the specified value.
- **`findProductsByNameContaining(@Param("nameFragment") String nameFragment)`**: Uses a JPQL `LIKE` query to find products whose `name` contains the provided fragment.
- **`findProductsByWeightRange(@Param("minWeight") Double minWeight, @Param("maxWeight") Double maxWeight)`**: Finds products whose `weight` falls within the specified range using JPQL's `BETWEEN`.
- **`findAllProductsOrderedByWeightDesc()`**: Uses a native SQL query to find all products and order them by `weight` in descending order.


## Resources
* https://spring.io/projects/spring-data
* https://docs.spring.io/spring-data/relational/reference/
* https://docs.spring.io/spring-data/jpa/reference/
* https://docs.spring.io/spring-data/mongodb/reference/
* https://www.baeldung.com/spring-data-repositories
