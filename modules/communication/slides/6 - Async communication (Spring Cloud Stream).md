# Asynchronous communications (Spring Cloud Stream)

## Introduction
Spring Cloud Stream also allows to abstract away the implementation details of the messaging platform that we’re using. We can use multiple message platforms, including Apache Kafka and RabbitMQ, and the platform’s implementation-specific details are kept out of the application code. The implementation of message publication and consumption in your application is done through platform-neutral Spring interfaces.

Let’s begin our discussion by looking at the Spring Cloud Stream architecture through the lens of two services communicating via messaging. One service is the message publisher, and one service is the message consumer.

![](images/spring-cloud-stream-architecture.avif)

A **source** takes a Plain Old Java Object (POJO), which represents the message to be published, serializes it (the default serialization is JSON), and publishes the message to a channel.

A **channel** is an abstraction over the queue that’s going to hold the message. It is always associated with a target queue name, but that queue name is never directly exposed to the code, which means that we can switch the queues the channel reads or writes without changing the application’s code (only the configuration).

A **binder** talks to a specific message platform. The binder part of the Spring Cloud Stream framework allows us to work with messages without having to be exposed to platform-specific libraries and APIs for publishing and consuming messages.

A **sink** listens to a channel for incoming messages and deserializes the message back into a POJO object. From there, the message can be processed by the business logic of the Spring service.

## Defining events
Messaging systems handle messages that typically consist of headers and a body. An event is a message that describes something that has happened. For events, the message body can be used to describe the type of event, the event data, and a timestamp for when the event occurred.

An event could be defined by the following:
* The type of event, for example, a create or delete event
* A key that identifies the data (e.g., a message ID)
* A data element, that is, the actual data in the event
* A timestamp, which describes when the event occurred

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

    public Event(@NonNull Type eventType, K key, T data) {
        this.eventType = eventType;
        this.key = key;
        this.data = data;
        this.eventCreatedAt = ZonedDateTime.now();
    }
}
```


## Project dependencies

To include Spring Cloud Stream in our project, we need to add *spring-cloud-stream* and at least one binder (e.g., spring-cloud-starter-stream-rabbit or spring-cloud-starter-stream-kafka) as shown below.

```
    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2023.0.0</spring-cloud.version>
    </properties>
    
    <dependencies>
        ...
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-stream</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-stream-rabbit</artifactId>
        </dependency>
        ...
    </dependencies>
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
```

## Publishing events
To publish an event we need to:
* Create an Event object
* Use the *StreamBridge* class to publish events on the desired topic
* Add configuration required for publishing events

```java
@Component
public class EventSender {
    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();
    private static final Logger LOG = LoggerFactory.getLogger(EventSender.class);
    private final StreamBridge streamBridge;

    public EventSender(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @Scheduled(fixedRate = 1000)
    public void randomMessage() {
        int index = RANDOM.nextInt(Event.Type.class.getEnumConstants().length);
        Event<String, Integer> event = new Event(
                Event.Type.class.getEnumConstants()[index],
                UUID.randomUUID().toString(),
                RANDOM.nextInt(100)
        );
        sendMessage("message-out-0", event);
    }

    private void sendMessage(String bindingName, Event<String, Integer> event) {
        for (int i = 0; i < 5; i++) {
            Message<Event<String, Integer>> message = MessageBuilder.withPayload(event)
                    .setHeader("partitionKey", event.getKey())
                    .build();
            LOG.info("Sending message {} to {}", event, bindingName);
            streamBridge.send(bindingName, message);
        }
    }
}
```

We also need to set up the configuration for the messaging system, to be able to publish events. In particular, we need to provide RabbitMQ as the default messaging system (including connectivity information), JSON as the default content type, and which topics should be used.

```yaml
server.port: 8081

spring.rabbitmq:
  host: 127.0.0.1
  port: 5672
  username: guest
  password: guest

spring.cloud.stream:
  defaultBinder: rabbit
  default.contentType: application/json
  bindings:
    message-out-0:
      destination: messages
```

## Receiving events

To be able to consume events, we need to do the following:
* Declare message processors that consume events published on specific topics
* Add configuration required for consuming events

The message receiver (frequently called *processor*) is declared as below. From the code, we can see that:
* The class is annotated with *@Configuration*, telling Spring to look for Spring beans in the class.
* We declare a Spring bean that implements the functional interface *Consumer*, accepting an event as an input parameter of type Event<String,Integer>.

```java
@Configuration
public class EventReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(EventReceiver.class);

    @Bean
    public Consumer<Event<String, Integer>> messageProcessor() {
        return event -> {
            switch (event.getEventType()) {
                case CREATE:
                    LOG.info(String.format("[CREATE] --> %s", event));
                    break;
                case DELETE:
                    LOG.info(String.format("[DELETE] --> %s", event));
                    break;
                case UPDATE:
                    LOG.info(String.format("[UPDATE] --> %s", event));
                    break;
                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE/DELETE/UPDATE event";
                    throw new RuntimeException(errorMessage);
            }
        };
    }
}
```

We also need to set up a configuration for the messaging system to be able to consume events. To do this, we need to complete the following steps:

```yaml
server.port: 8082

spring.rabbitmq:
  host: 127.0.0.1
  port: 5672
  username: guest
  password: guest

spring.cloud.function.definition: messageProcessor

spring.cloud.stream:
  defaultBinder: rabbit
  default.contentType: application/json
  bindings.messageProcessor-in-0:
    destination: messages
```

## Trying out the messaging system


In this section, we will test the microservices together with [LavinMQ](https://lavinmq.com/). LavinMQ is an extremely fast Message Broker that handles a large amounts of messages and connections. It implements the AMQP protocol and runs on a single node in order to maximize performance, yet without compromising availability.

The default *docker-compose.yml* is used for this configuration. 

```
services:
  publisher:
    image: async-rabbitmq-publisher
    build: async-rabbitmq-publisher
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      lavinmq:
        condition: service_healthy
    deploy:
      mode: replicated
      replicas: 1

  consumer:
    image: async-rabbitmq-consumer
    build: async-rabbitmq-consumer
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      lavinmq:
        condition: service_healthy
    deploy:
      mode: replicated
      replicas: 1

  lavinmq:
    image: cloudamqp/lavinmq:latest
    mem_limit: 512m
    ports:
      - 5672:5672
      - 15672:15672
    healthcheck:
      test: [ "CMD", "lavinmqctl", "status" ]
      interval: 5s
      timeout: 2s
      retries: 60
```

Start the system landscape with the following commands:

```
$ mvn clean package
$ docker compose build
$ docker compose up --detach
```

Using the [web interface](http://localhost:15672/) of LavinMQ (login: guest/guest) it is possible to observe that the *messages* exchange receives 5 events/s and publishes the same events on two separate (anonymous) queues. The output rate is, as a consequence, 10 events/s.

![](images/rabbitmq-two-queues.avif)

### Replicas

### Consumer groups
The problem is, if we scale up the number of instances of a message consumer, both instances of the product microservice will consume the same messages. We can avoid this issue by making use of *consumer groups*. The *docker-compose-groups.yml* embeds the needed configuration. Particularly, it activates the *groups* profile to change the configuration of both message consumers.

```
  ...
  consumer-0:
    build: consumer-end
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker,groups
    depends_on:
      lavinmq:
        condition: service_healthy

  consumer-1:
    build: consumer-end
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker,groups
    depends_on:
      lavinmq:
        condition: service_healthy
  ...
```

The *groups* profile, add the following configurations in both the consumers (see *application.yml*):

```
spring.config.activate.on-profile: groups
spring.cloud.stream:
  bindings:
    messageProcessor-in-0:
      group: messagesGroup
```

Start the system landscape with the following commands:

```
$ mvn clean package -Dmaven.test.skip=true 
$ docker compose build
$ export COMPOSE_FILE=docker-compose-groups.yml
$ docker compose up -d
```

Using the [web interface](http://localhost:15672/) of LavinMQ/RabbitMQ it is possible to observe that the messages exchange receives 5 events/s and publishes the same events on one (named) queue. Each event is consumed once by only one consumer. Thus, the output rate is 5 events/s.

![](images/rabbitmq-one-group.avif)

### Partitions

The problem is, each event is received by only one consumer. However, we do have any guarantee that all the messages concerning the same ID (e.g. the same product) reach the same consumer instance. This might lead to misbehaviour. To solve this issue, we can activate the use of *partitions* with Spring profiles.

```
  ...
  producer:
    build: producer-end
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker,partitioned
    depends_on:
      lavinmq:
        condition: service_healthy

  consumer-0:
    build: consumer-end
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker,groups,partitioned,partitioned_instance_0
    depends_on:
      lavinmq:
        condition: service_healthy

  consumer-1:
    build: consumer-end
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker,groups,partitioned,partitioned_instance_1
    depends_on:
      lavinmq:
        condition: service_healthy
  ...
```

The *partitioned* profile, adds the following configurations.

Producer-side:
```
spring.config.activate.on-profile: partitioned

spring.cloud.stream.bindings.message-out-0.producer:
  partition-key-expression: headers['partitionKey']
  partition-count: 2
```

Consumer-side:
```
spring.config.activate.on-profile: partitioned
spring.cloud.stream:
  bindings:
    messageProcessor-in-0:
      consumer:
        partitioned: true
        instanceCount: 2

---
spring.config.activate.on-profile: partitioned_instance_0
spring.cloud.stream.bindings.messageProcessor-in-0.consumer:
  instanceIndex: 0

---
spring.config.activate.on-profile: partitioned_instance_1
spring.cloud.stream.bindings.messageProcessor-in-0.consumer:
  instanceIndex: 1
```

Start the system landscape with the following commands:

```
$ mvn clean package -Dmaven.test.skip=true 
$ docker compose build
$ export COMPOSE_FILE=docker-compose-partitions.yml
$ docker compose up -d
```

Using the [web interface](http://localhost:15672/) of LavinMQ/RabbitMQ it is possible to observe that the messages exchange receives 5 events/s and publishes the same events on one (named) queue. Each event is consumed once by only one consumer. Thus, the output rate is 5 events/s. However, by checking the logs, it is possible to observe how each consumer receive *all* five messages pertaining to same ID.

![](images/rabbitmq-two-partitions.avif)

### Retries and dead-letter queues
The `auditgroup` profile implements retries and a dead-letter queue.
```
spring.config.activate.on-profile: auditgroup
spring.cloud.stream.bindings.messageProcessor-in-0.consumer:
  maxAttempts: 3
  backOffInitialInterval: 500
  backOffMaxInterval: 1000
  backOffMultiplier: 2.0

spring.cloud.stream.rabbit.bindings.messageProcessor-in-0.consumer:
  autoBindDlq: true
  republishToDlq: true
  dead-letter-exchange: messages.dl
  dead-letter-queue-name: messages.dlq
```

- `maxAttempts` maximum number of retries to consume a message in case of error;
- `backOffInitialInterval` initial backoff interval (in milliseconds) before the first retry;
- `backOffMaxInterval` maximum backoff interval (in milliseconds) between retries;
- `backOffMultiplier` multiplier used to calculate the backoff interval between subsequent retries.

The **DLQ** receives messages if there have been errors (queue size exceeded, runtime exception, etc.).
- `republishToDlq` indicates that messages should be republished on the DLQ in the event of an error;
- `dead-letter-exchange` and `dead-letter-queue-name` they specify the names of the components that we need to create in the RabbitMQConfig.java file.
```
@Configuration
@Profile("auditgroup")
public class RabbitMQConfig {

    @Value("${spring.cloud.stream.rabbit.bindings.messageProcessor-in-0.consumer.dead-letter-exchange}")
    private String deadLetterExchange;

    @Value("${spring.cloud.stream.rabbit.bindings.messageProcessor-in-0.consumer.dead-letter-queue-name}")
    private String deadLetterQueue;

    @Bean
    public FanoutExchange deadLetterExchange() {
        return new FanoutExchange(deadLetterExchange);
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(deadLetterQueue);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange());
    }
}
```

We take the names defined in the application.yml and create the dead-letter exchange as FanoutExchange, the dead-letter queue and the binding between the two.

By connecting to the link http://localhost:15672/#/queues (if you use LavinMQ) you can see the queues created and the messages inserted into the queue.

## Resources
