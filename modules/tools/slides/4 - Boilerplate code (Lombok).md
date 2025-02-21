# Boilerplate Code Reduction with Lombok

In this guide, we will explore how to significantly reduce the amount of boilerplate code in Java. While Java is a powerful language, it often becomes verbose when handling routine tasks or complying with framework practices. This verbosity doesn't add much business value, and that's where Lombok comes in to make us more productive.

## Project Dependencies

To include Lombok in your project, you can choose between two approaches:

* If you're creating a new project, simply add the dependency through the [Spring Boot Initializr](https://start.spring.io/)
* If you want to add Lombok to an existing project (and you're using Maven), include the following dependency in your `pom.xml`

  ```xml
  <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
  </dependency>
  ```
  
## Hands-on Example

Let's suppose we need to define an event in a class. Specifically, our event will be characterized by:

* The type of event (e.g., create, delete, or update)
* A key identifying the data (e.g., a message ID)
* A data element (the actual data in the event)
* A timestamp indicating when the event occurred

To fully implement constructors, getters, setters, and other methods, we'd end up with something like this:

```java
public class Event<K, T> {
    public enum Type {CREATE, DELETE, UPDATE}
    private Type eventType;
    private K key;
    private T data;
    private ZonedDateTime eventCreatedAt;

    public Event() {}

    public Event(Type eventType, K key, T data) {
        this.eventType = eventType;
        this.key = key;
        this.data = data;
        this.eventCreatedAt = ZonedDateTime.now();
    }
    
    public Type getEventType() {
        return eventType;
    }

    public K getKey() {
        return key;
    }

    public T getData() {
        return data;
    }

    public ZonedDateTime getEventCreatedAt() {
        return eventCreatedAt;
    }

    @Override
    public String toString() {
        return "Event{" + "eventType=" + eventType + ", key=" + key + ", data=" + data + ", eventCreatedAt=" + eventCreatedAt + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event<?, ?> event = (Event<?, ?>) o;
        return eventType == event.eventType &&
                Objects.equals(key, event.key) &&
                Objects.equals(data, event.data) &&
                Objects.equals(eventCreatedAt, event.eventCreatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, key, data, eventCreatedAt);
    }
}
```

Creating a random Event object looks like:

```java
int index = RANDOM.nextInt(Event.Type.class.getEnumConstants().length);
Event<String, Integer> event = new Event(
    Event.Type.class.getEnumConstants()[index],
    UUID.randomUUID().toString(),
    RANDOM.nextInt(100)
);
```

This process is tedious and cluttered, affecting readability. IDEs can auto-generate code for you, but this still doesn't address the underlying issue of verbose code. Enter Lombok!

### @NoArgsConstructor, @AllArgsConstructor, and @RequiredArgsConstructor

Lombok provides annotations to avoid manually writing constructors:

* `@NoArgsConstructor` generates a no-arguments constructor. If not possible due to final fields, the compiler will throw an error unless you use `@NoArgsConstructor(force = true)`, which initializes final fields with default values (0, false, or null). Fields with constraints like `@NonNull` won't be checked, so ensure those fields are initialized later.
* `@AllArgsConstructor` generates a constructor with one parameter for each field. Fields marked with `@NonNull` include null checks.
* `@RequiredArgsConstructor` generates a constructor for all uninitialized final fields and fields marked `@NonNull`. A null check is added for `@NonNull` fields.

Here’s how our class looks after using Lombok constructors:

```java
@NoArgsConstructor
@AllArgsConstructor
public class Event<K, T> {
    public enum Type {CREATE, DELETE, UPDATE}
    private Type eventType;
    private K key;
    private T data;
    private ZonedDateTime eventCreatedAt;

    public Type getEventType() {
        return eventType;
    }

    public K getKey() {
        return key;
    }

    public T getData() {
        return data;
    }

    public ZonedDateTime getEventCreatedAt() {
        return eventCreatedAt;
    }

    @Override
    public String toString() {
        return "Event{" + "eventType=" + eventType + ", key=" + key + ", data=" + data + ", eventCreatedAt=" + eventCreatedAt + '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event<?, ?> event = (Event<?, ?>) o;
        return eventType == event.eventType &&
                Objects.equals(key, event.key) &&
                Objects.equals(data, event.data) &&
                Objects.equals(eventCreatedAt, event.eventCreatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, key, data, eventCreatedAt);
    }
}
```

Since we have no `@NonNull` or final fields, `@RequiredArgsConstructor` isn't necessary.

### @Getter and @Setter

The `@Getter` and `@Setter` annotations auto-generate getters and setters for all fields, further simplifying the code:

```java
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Event<K, T> {
    public enum Type {CREATE, DELETE, UPDATE}
    private Type eventType;
    private K key;
    private T data;
    private ZonedDateTime eventCreatedAt;

    @Override
    public String toString() {
        return "Event{" + "eventType=" + eventType + ", key=" + key + ", data=" + data + ", eventCreatedAt=" + eventCreatedAt + '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event<?, ?> event = (Event<?, ?>) o;
        return eventType == event.eventType &&
                Objects.equals(key, event.key) &&
                Objects.equals(data, event.data) &&
                Objects.equals(eventCreatedAt, event.eventCreatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, key, data, eventCreatedAt);
    }
}
```

### @ToString and @EqualsAndHashCode

Lombok can also auto-generate `toString()`, `equals()`, and `hashCode()` methods:

```java
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Event<K, T> {
    public enum Type {CREATE, DELETE, UPDATE}
    private Type eventType;
    private K key;
    private T data;
    private ZonedDateTime eventCreatedAt;
}
```

Now our class consists only of fields and Lombok annotations!

### @Data

For even greater simplicity, the `@Data` annotation bundles `@ToString`, `@EqualsAndHashCode`, `@Getter`, `@Setter`, and `@RequiredArgsConstructor`. Our class can now be rewritten as:

```java
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Event<K, T> {
    public enum Type {CREATE, DELETE, UPDATE}
    @NonNull private Type eventType;
    private K key;
    private T data;
    private ZonedDateTime eventCreatedAt;
}
```

If we introduce final or `@NonNull` fields, the appropriate constructors are automatically generated by Lombok.

### @Builder and @Builder.Default

The `@Builder` annotation allows for more readable object creation, particularly useful in complex classes:

```java
Event event = Event.builder()
  .eventType(Event.Type.CREATE)
  .key(UUID.randomUUID().toString())
  .data(RANDOM.nextInt(100))
  .eventCreatedAt(ZonedDateTime.now())
  .build();
```

You can even set default values with `@Builder.Default`:

```java
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Event<K, T> {
    public enum Type {CREATE, DELETE, UPDATE}
    @Builder.Default private Type eventType = Type.CREATE;
    @Builder.Default private K key = UUID.randomUUID().toString();
    private T data;
    @Builder.Default private ZonedDateTime eventCreatedAt = ZonedDateTime.now();
}
```

This way, object creation can be simplified, and default values will be used unless overridden:

```java
Event event = Event.builder()
  .data(RANDOM.nextInt(100))
  .build();
```

## Resources
* https://projectlombok.org/