# Communication styles (Spring Cloud Stream)

## Introduction
Spring Cloud Stream also allows to abstract away the implementation details of the messaging platform that we’re using. We can use multiple message platforms, including Apache Kafka and RabbitMQ, and the platform’s implementation-specific details are kept out of the application code. The implementation of message publication and consumption in your application is done through platform-neutral Spring interfaces.

Let’s begin our discussion by looking at the Spring Cloud Stream architecture through the lens of two services communicating via messaging. One service is the message publisher, and one service is the message consumer.

![](../../../slides/images/spring-cloud-stream-architecture.avif)

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

```
public class Event<K, T> {
    public enum Type {CREATE, DELETE, UPDATE}
    private Type eventType;
    private K key;
    private T data;
    private ZonedDateTime eventCreatedAt;

    public Event() {
    }

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

```
@Component
public class MessageSender {
    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();
    private static final Logger LOG = LoggerFactory.getLogger(MessageSender.class);
    private final StreamBridge streamBridge;

    public MessageSender(StreamBridge streamBridge) {
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
        LOG.debug("Sending message {} to {}", event, bindingName);
        for (int i = 0; i < 5; i++) {
            Message<Event<String, Integer>> message = MessageBuilder.withPayload(event)
                    .setHeader("partitionKey", event.getKey())
                    .build();
            streamBridge.send(bindingName, message);
        }
    }
}
```

We also need to set up the configuration for the messaging system, to be able to publish events. In particular, we need to provide RabbitMQ as the default messaging system (including connectivity information), JSON as the default content type, and which topics should be used.

```
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

```
@Configuration
public class MessageReceiver {

    private static final Logger LOG = LoggerFactory.getLogger(MessageReceiver.class);

    @Bean
    public Consumer<Event<String, Integer>> messageProcessor() {
        return event -> LOG.info(String.format("--> %s", event));
    }
}
```

We also need to set up a configuration for the messaging system to be able to consume events. To do this, we need to complete the following steps:

```
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


### LavinMQ

In this section, we will test the microservices together with [LavinMQ](https://lavinmq.com/). LavinMQ is an extremely fast Message Broker that handles a large amounts of messages and connections. It implements the AMQP protocol and runs on a single node in order to maximize performance, yet without compromising availability.

The default *docker-compose.yml* is used for this configuration. 

```
services:
  producer:
    build: producer-end
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      lavinmq:
        condition: service_healthy

  consumer-0:
    build: consumer-end
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      lavinmq:
        condition: service_healthy

  consumer-1:
    build: consumer-end
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    depends_on:
      lavinmq:
        condition: service_healthy

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
$ mvn clean package -Dmaven.test.skip=true 
$ docker compose build
$ docker compose up -d
```

Using the [web interface](http://localhost:15672/) of LavinMQ (login: guest/guest) it is possible to observe that the *messages* exchange receives 5 events/s and publishes the same events on two separate (anonymous) queues. The output rate is, as a consequence, 10 events/s.

![](../../../slides/images/rabbitmq-two-queues.avif)

### LavinMQ with consumer groups
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

![](../../../slides/images/rabbitmq-one-group.avif)

### LavinMQ with partitions

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

![](../../../slides/images/rabbitmq-two-partitions.avif)

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

## Apache Kafka
To set up a Kafka cluster using Docker Compose, it is essential to define the two main services in the YAML file: ZooKeeper and Kafka, using the images provided by Confluent.
```
  zookeeper:
    image: confluentinc/cp-zookeeper:7.2.2
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:7.2.2
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
      - "29092:29092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
    healthcheck:
      test: [ "CMD-SHELL", "nc -zv localhost 9092 || exit 1" ]
      interval: 5s
      timeout: 2s
      retries: 60
```
This example uses a producer to send messages to a topic `messages` and two consumers who consume the topic's messages. At startup, each consumer will be assigned a list of topic partitions from which to read data.

As configured, a partition can only be read by one consumer. If you associate other microservices listening on that topic, the Kafka broker will automatically update the list of partitions associated with consumers.

In the `application.yml` (or `application.properties`) we indicate the microservice configurations for the kafka broker.
```
spring:
  kafka:
    bootstrap-servers: kafka:9092
    consumer:
      group-id: messages-group
      auto-offset-reset: earliest
      value-deserializer: com.baeldung.ls.common.EventDeserializer
    producer:
      acks: all
      value-serializer: com.baeldung.ls.common.EventSerializer
```
**Consumer configuration**:
- `bootstrap-servers` specifies the address of the Kafka server to which the application connects;
- `group-id defines` the consumer group to which this consumer belongs;
- `auto-offset-reset: earliest` tells Kafka to start reading messages from the oldest available offset if no initial offset exists for the consumer group. Other options are `latest` (to read from the most recent) and `none` (to throw an error if no leading offset exists).
- `value-deserializer`specifies the class to use to deserialize the message values consumed by Kafka

**Producer configuration**:
- `acks: all` producers consider messages as "written successfully" when the message is accepted by all in-sync replicas;
- `value-serializer` specifies the class to use to serialize the values of messages sent to Kafka.

Topics are created and errors are handled in the `KafkaConfig.java` file.

**Creating a topic with 1 replica and 5 partitions**
```
@Bean
public NewTopic topicOrders() {
    return TopicBuilder.name(TOPIC_MESSAGES)
            .partitions(5)
            .replicas(1)
            .build();
}
```

**Error handling and posting messages to a Dead Letter Topic (DLT)**
```
@Bean
public CommonErrorHandler errorHandler(KafkaOperations<Object, Object> template) {
    return new DefaultErrorHandler(
            new DeadLetterPublishingRecoverer(template), new FixedBackOff(1000L, 2));
}
```
The DLT topic is not created automatically and must be created manually with the notation `<topic_name>.DLT`.
Before publishing the messages to the DLT, I set two retries at an interval of 1000ms.

To be able to consume the messages of `messages` topic, insert the following notation to a java receive function.
```
@KafkaListener(topics = KafkaConfig.TOPIC_MESSAGES, groupId = KafkaConfig.GROUP_ID)
```