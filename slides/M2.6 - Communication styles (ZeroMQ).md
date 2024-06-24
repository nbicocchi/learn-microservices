# ZeroMQ examples

## Adding the dependency

Considering we're using maven, we need to add the following dependency in our pom.xml:

```xml
<dependency>
	<groupId>org.zeromq</groupId>
	<artifactId>jeromq</artifactId>
</dependency>
```

Unfortunately, it is not possible to add the dependency through the [spring boot initializr](https://start.spring.io).

## Common parts

In this section, we will specifically discuss the common code between the two examples we will see: the first concerns the ROUTER-DEALER pattern, while the second concerns the PUB-SUB pattern.

### Event class

This class implements the data that our endpoints will exchange during communication.

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event<K, T> {

    public enum Type {CREATE, DELETE, UPDATE}

    @JsonProperty("eventType") private Type eventType;

    @JsonProperty("key") private K key;

    @JsonProperty("data") private T data;

    @Builder.Default
    @JsonProperty("eventCreatedAt")
    private ZonedDateTime eventCreatedAt = ZonedDateTime.now();
}
```

Using Lombok annotations (check the [explanation]('Reducing boilerplate code (Lombok).md')) we've been able to implement this class simply through fields declaration. More specifically we have:

* Type&rarr;enum that specifies values fot eventType filed;
* eventType &rarr;store the event type;
* key&rarr;it represents the event's ID (we'll generate a random UUID);
* data&rarr;data to be sent;
* eventCreatedAt&rarr;date and time of event generation.

For all parameters we had to specify @JsonProperty to be able to map the event object in a json serialization. In this way we're able to exchange a string in json format and de-serialize that obtaining back an event object.

### Object mapper

This class define an object we'll use to serialize/de-serialize events to be transported as json formatted strings. We are generating a bean which will be injected in other classes by the @Autowired annotation from spring boot.

```java
@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper getObjectMapper() {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;

    }

}
```

In particular, *objectMapper.registerModule(new JavaTimeModule())* let us to serialize properly the *eventCreatedAt* field in event class.

## DEALER - ROUTER pattern

### Dealer

First of all, as we've done for the ObjectMapper, we are going to define a Bean also for configuring the socket.

```java
@Configuration
public class ZmqSocketConfig {

    @Value("${IDENTITY}")
    private String identity;
    
    @Value("${ROUTERS_NAMES}")
    private String routersNames;
    
    @Value("${DELIMITER}")
    private String delimiter;

    @Value("${router.port}")
    private int routerPort;
    
    @Bean(destroyMethod = "close")
    public ZMQ.Socket zmqDealer(){

        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket dealer = context.socket(SocketType.DEALER);
        
        dealer.setIdentity(this.identity.trim().getBytes(ZMQ.CHARSET));
        String[] routersNamesArray = this.routersNames.split(delimiter);

        for (String router :routersNamesArray) {
            String connectAddress = String.format("tcp://%s:%d", router.trim(), routerPort);
            dealer.connect(connectAddress);
        }

        return dealer;

    }
}
```

Dealer endpoint will connect to all routers endpoint and we must specify some env variables through the dockerfile and the router port by the application.properties.

```yaml
services:
  dealer:
    build: ./dealer
    container_name: dealer
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - IDENTITY=dealer-0
      - ROUTERS_NAMES=router-0,router-1
      - DELIMITER=,
    depends_on:
      router-0:
          condition: service_healthy
      router-1:
        condition: service_healthy
```

```properties
spring.application.name=dealer
router.port=5555
```

Now we're ready to inject this configuration in dealer class which we'll be our event generator. Here, every second, we:

* create an event;
* serialize it;
* send it to routers.

```java
@Slf4j
@Component
public class EventDealer {

    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();

    private ObjectMapper objectMapper;

    private ZMQ.Socket dealer;

    @Autowired
    public EventDealer(ZMQ.Socket dealer, ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

        this.dealer = dealer;

    }

    @Scheduled(fixedRate = 1000)
    @SneakyThrows(JsonProcessingException.class)
    public void eventProduction() {
        Event event = Event.builder()
                .data("Hello World!")
                .key(UUID.randomUUID().toString())
                .eventType(Event.Type.class.getEnumConstants()[RANDOM.nextInt(Event.Type.class.getEnumConstants().length)])
                .build();
        String stringEvent = objectMapper.writeValueAsString(event);
        log.info("Sent: {}", stringEvent);
        dealer.send(stringEvent);
    }
}
```

### Routers

As we've done for the dealer, we must configure the socket and to be more clean in doing that we define another Bean for this configuration.

```java
@Configuration
public class ZmqSocketConfig {

    @Value("${router.port}")
    private int routerPort;

    @Value("${router.name}")
    private String routerName;

    @Bean(destroyMethod = "close")
    public ZMQ.Socket zmqRouter(){

        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket router = context.socket(SocketType.ROUTER);
        String bindAddress = String.format("tcp://%s:%d", routerName, routerPort);
        router.bind(bindAddress);

        return router;

    }

}
```

This time, all infos are specified in the application.properties file.

```properties
spring.application.name=router
router.port=5555
router.name=*
```

Anyway, we're going to run more routers, so we're going to specify names in dockerfile.

```yaml
services:
  router-0:
    build: ./router
    container_name: router-0
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    healthcheck:
      test: ["CMD", "true"]
      interval: 5s
      timeout: 2s
      retries: 60

  router-1:
    build: ./router
    container_name: router-1
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    healthcheck:
      test: [ "CMD", "true" ]
      interval: 5s
      timeout: 2s
      retries: 60

```

Now, we're ready to receive messages from the dealer defining the router class in which we:

* receive the message;
* de-serialize it;
* log the event.

```java
@Slf4j
@Component
public class EventRouter {

    private ObjectMapper objectMapper;

    private ZMQ.Socket router;

    @Autowired
    public EventRouter(ZMQ.Socket router, ObjectMapper objectMapper) {

        this.router = router;

        this.objectMapper = objectMapper;

    }

    @Scheduled(fixedRate = 2500)
    @SneakyThrows(JsonProcessingException.class)
    public void eventHandling() {

        ZMsg msg = ZMsg.recvMsg(router);

        String sock_identity = new String(msg.pop().getData(), ZMQ.CHARSET);
        //log.info("Identity: {}", sock_identity);
        String message = new String(msg.pop().getData(), ZMQ.CHARSET);
        Event event = objectMapper.readValue(message, Event.class);
        log.info("Received: {}", event);
    }

}
```

### Results

To run this pattern go in **dealer** folder and run the following command:

```shell
./mvnw clean package -DskipTests
```

Now go in **router** folder and run the same command.
Lastly, go back in **dealer-router** folder and run:

```shell
docker compose up --build
```

Now you should see containers starting in your terminal and notice that dealer writes events while one single router get it.

![](images/Dealer-Router_Output.png)

## PUB - SUB pattern

### Publisher

Another time we're going to define a Bean for the socket configuration.

```java
@Configuration
public class ZmqSocketConfig {

    @Value("${publisher.port}")
    private int publisherPort;

    @Value("${publisher.name}")
    private String publisherName;

    @Bean(destroyMethod = "close")
    public ZMQ.Socket zmqPublisher() {

        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket publisher = context.socket(SocketType.PUB);

        String bindAddress = String.format("tcp://%s:%d", publisherName, publisherPort);
        publisher.bind(bindAddress);

        return publisher;
    }

}
```

Publisher will bind an address on a specific port passed by application.properties file as following:

```properties
spring.application.name=publisher
publisher.port=5555
publisher.name=*
```

Lastly we must specify container characteristics:

```yaml
services:
  publisher:
    build: ./publisher
    container_name: publisher
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    healthcheck:
      test: ["CMD", "true"]
      interval: 5s
      timeout: 2s
      retries: 60
```

Now we're ready to define the class in which we:

* create the event;
* serialize it;
* publish it.

```java
@Slf4j
@Component
public class EventPublisher {

    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();

    private ObjectMapper objectMapper;

    private ZMQ.Socket publisher;

    @Autowired
    public EventPublisher(ZMQ.Socket publisher, ObjectMapper objectMapper){

        this.publisher = publisher;
        this.objectMapper = objectMapper;

    }

    @Scheduled(fixedRate = 1000, initialDelay = 8000)
    @SneakyThrows(JsonProcessingException.class)
    public void eventPublish() {

        Event event = Event.builder()
                .data("Hello World!")
                .key(UUID.randomUUID().toString())
                .eventType(Event.Type.class.getEnumConstants()[RANDOM.nextInt(Event.Type.class.getEnumConstants().length)])
                .build();

        String topic = "topic-" + RANDOM.nextInt(0,3);
        log.info("Choosen topic is {}", topic);

        String stringEvent = objectMapper.writeValueAsString(event);
        publisher.sendMore(topic.getBytes(ZMQ.CHARSET));
        publisher.send(stringEvent.getBytes(ZMQ.CHARSET));
        log.info("Event published: {}", stringEvent);

    }
}
```

We've specified an initialDelay of 8s to be sure that all subscribers are ready to receive messages.

### Subscribers

For the last time, we define the configuration for the socket (subscribers will connect to the publisher):

```java
@Configuration
public class ZmqSocketConfig {

    @Value("${publisher.port}")
    private int publisherPort;

    @Value("${publisher.name}")
    private String publisherName;

    @Value("${TOPIC_NAMES}")
    private String topicNamesEnv;

    @Bean(destroyMethod = "close")
    public ZMQ.Socket zmqSubscriber() {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket subscriber = context.socket(SocketType.SUB);

        String connectAddress = String.format("tcp://%s:%d", publisherName, publisherPort);
        subscriber.connect(connectAddress);
        String[] topics = topicNamesEnv.split(",");
        for (String topic : topics) {
            subscriber.subscribe(topic.trim().getBytes(ZMQ.CHARSET));
        }

        return subscriber;
    }

}
```

and we specify publisher endpoint details through application.properties:

```properties
spring.application.name=consumer
publisher.port=5555
publisher.name=publisher
```

while creating an env variable for topic names through the dockerfile:

```yaml
services:
  subscriber-0:
    build: ./subscriber
    container_name: subscriber-0
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - TOPIC_NAMES=topic-0,topic-1
    depends_on:
      publisher:
        condition: service_healthy

  subscriber-1:
    build: ./subscriber
    container_name: subscriber-1
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - TOPIC_NAMES=topic-1,topic-2
    depends_on:
      publisher:
        condition: service_healthy
```

You can notice that the goal is to make subscribers share a topic while being sub also for a second private topic to show that PUB-SUB patter actually works.

Now we can define the class in which we'll:

* receive messages;
* de-serialize them;
* log event.

```java
@Slf4j
@Component
public class EventSubscriber {

    private ObjectMapper objectMapper;

    private  ZMQ.Socket subscriber;

    @Autowired
    public EventSubscriber(ZMQ.Socket subscriber, ObjectMapper objectMapper) {

        this.subscriber = subscriber;
        this.objectMapper = objectMapper;

    }

    @Scheduled(fixedRate = 2500)
    @SneakyThrows(IOException.class)
    public void eventRetrieving(){

        String topic = subscriber.recvStr();
        log.info("Got event from topic: {}", topic);

        byte[] byteEvent = subscriber.recv();
        Event event = objectMapper.readValue(byteEvent, Event.class);
        log.info("Got event: {}", event);

    }
}
```

### Results

To run the code, exactly as we've done for the previous pattern, run the following command both in **publisher** and **subscriber** folder.

```bash
./mvnw clean package --build
```

Now go back in **pub-sub** folder and run docker compose command.

```shell
docker compose up --build
```

If everything is good, you should notice that subscriber-0 do not receive topic-2 messages and subscriber-1 do not receive topic-0 messages, but both are receiving topic-1 events.

![](images/Pub-Sub_Output.png)
