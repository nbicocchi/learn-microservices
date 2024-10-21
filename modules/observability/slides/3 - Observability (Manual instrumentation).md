# Observability (OTLP)

## Services instrumentation

For instrumentation, we make use of a Java Agent providing (zero-code instrumentation). 

1. For each service (i.e., eureka, datetime, datetime-composite) setup dependencies for manually exporting metrics, logs, and traces. Each service will expose metrics in Prometheus format, send logs to Loki and Traces to Jaeger.

```xml
	<properties>
		<java.version>21</java.version>
		<spring-cloud.version>2023.0.3</spring-cloud.version>
		<resilience4j.version>2.2.0</resilience4j.version>
		<micrometer-tracing.version>1.3.5</micrometer-tracing.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
		<dependency>
			<groupId>io.github.resilience4j</groupId>
			<artifactId>resilience4j-spring-boot3</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>
		<dependency>
			<groupId>io.micrometer</groupId>
			<artifactId>micrometer-registry-prometheus</artifactId>
			<scope>runtime</scope>
		</dependency>

		<dependency>
			<groupId>com.github.loki4j</groupId>
			<artifactId>loki-logback-appender</artifactId>
			<version>1.5.2</version>
		</dependency>

		<dependency>
			<groupId>io.micrometer</groupId>
			<artifactId>micrometer-tracing-bridge-otel</artifactId>
		</dependency>
		<dependency>
			<groupId>io.opentelemetry</groupId>
			<artifactId>opentelemetry-exporter-otlp</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
	</dependencies>
```

2. For each service, add the following bean for exporting traces directly to and OpenTelemetry endpoint, in our case Jaeger:

```java
@Configuration
public class AppConfig {
    @Bean
    OtlpHttpSpanExporter otlpHttpSpanExporter(@Value("${tracing.url}") String url) {
        return OtlpHttpSpanExporter.builder()
                .setEndpoint(url)
                .build();
    }
}
```

3. For each service, modify the *application.yml* configuration:

```yaml
management:
  endpoint:
    health:
      show-details: "ALWAYS"
  endpoints:
    web:
      exposure:
        include: "*"
  tracing:
    sampling:
      probability: 1.0

tracing:
  url: http://localhost:4318/v1/traces

logging:
  pattern:
    level: '%5p [${spring.application.name:},%X{traceId:-},%X{spanId:-}]'

---
spring.config.activate.on-profile: docker
eureka.client.serviceUrl.defaultZone: http://eureka:8761/eureka/

server:
  port: 8080

tracing:
  url: http://jaeger:4318/v1/traces
```

For each service, add inside the *src/main/resources* folder the following *logback.xml* file. This configures the LogBack daemon (the default implementation of slf4j) for sending logs to Loki.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{dd-MM-yyyy HH:mm:ss.SSS} [%thread] %-5level %logger{36}.%M - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/application.log</file>
        <encoder>
            <Pattern>%d{dd-MM-yyyy HH:mm:ss.SSS} [%thread] %-5level %logger{36}.%M - %msg%n</Pattern>
        </encoder>
    </appender>

    <appender name="LOKI" class="com.github.loki4j.logback.Loki4jAppender">
        <http>
            <url>${LOKI_ENDPOINT}</url>
        </http>
        <format>
            <label>
                <pattern>app=datetime-composite-service,host=${HOSTNAME}</pattern>
            </label>
            <message>
                <pattern>%-5level [%.5(${HOSTNAME})] %.10thread %logger{20} | %msg %ex</pattern>
            </message>
        </format>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="LOKI"/>
    </root>
</configuration>
```

3. Using environment variables, instruct each service to send its logs to the correct Loki endpoint.

```yaml
  eureka:
    build: eureka-service
    environment:
      - LOKI_ENDPOINT=http://loki:3100/loki/api/v1/push
      - SPRING_PROFILES_ACTIVE=docker
    mem_limit: 512m
    ports:
      - "8761:8761"

  datetime-composite:
    build: datetime-composite-service
    environment:
      - LOKI_ENDPOINT=http://loki:3100/loki/api/v1/push
      - SPRING_PROFILES_ACTIVE=docker
    mem_limit: 512m
    ports:
      - "8080:8080"

  datetime:
    build: datetime-service
    environment:
      - LOKI_ENDPOINT=http://loki:3100/loki/api/v1/push
      - SPRING_PROFILES_ACTIVE=docker
    mem_limit: 512m
```

## Metrics backend

1. Add the Prometheus backend to the service ecosystem

```yaml
    image: prom/prometheus:latest
    volumes:
      - ./config/prometheus.yaml:/etc/prometheus.yaml
    command:
      - --config.file=/etc/prometheus.yaml
      - --web.enable-remote-write-receiver
      - --enable-feature=exemplar-storage
      - --enable-feature=native-histograms
    ports:
      - "9090:9090"
```

2. Configure it using the configuration file provided to the container with volumes. Please note the two different scraping jobs. One for the application, the other for infrastructure components.

```yaml
scrape_configs:
  - job_name: 'application'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 5s
    static_configs:
      - targets: ['eureka:8761', 'datetime:8080', 'datetime-composite:8080' ]

  - job_name: 'infrastructure'
    metrics_path: '/metrics'
    scrape_interval: 5s
    static_configs:
      - targets: [ 'localhost:9090', 'otel-collector:8888', 'otel-collector:8889', 'jaeger:14269' ]
```



## Logs backend

1. Add the Loki backend to the service ecosystem

```yaml
  loki:
    image: grafana/loki:latest
    command: -config.file=/etc/loki/loki.yaml
    volumes:
      - ./config/loki.yaml:/etc/loki/loki.yaml
    ports:
      - "3100:3100"
```

2. Configure it using the configuration file provided to the container with volumes.

```yaml

# This is a complete configuration to deploy Loki backed by the filesystem.
# The index will be shipped to the storage via tsdb-shipper.

auth_enabled: false

server:
  http_listen_port: 3100

common:
  ring:
    instance_addr: 127.0.0.1
    kvstore:
      store: inmemory
  replication_factor: 1
  path_prefix: /tmp/loki

schema_config:
  configs:
    - from: 2020-05-15
      store: tsdb
      object_store: filesystem
      schema: v13
      index:
        prefix: index_
        period: 24h

storage_config:
  filesystem:
    directory: /tmp/loki/chunks

limits_config:
  allow_structured_metadata: true

pattern_ingester:
  enabled: true
```

## Traces backend

1. Add the Jaeger backend to the service ecosystem

```yaml
  jaeger:
    image: jaegertracing/all-in-one:latest
    environment:
      - METRICS_STORAGE_TYPE=prometheus
    ports:
      - "16686:16686"
```

2. The default configuration is OK.



## Grafana frontend

1. Add the Grafana backend to the service ecosystem


```yaml
  grafana:
    image: grafana/grafana:latest
    environment:
      - GF_AUTH_ANONYMOUS_ENABLED=true
      - GF_AUTH_ANONYMOUS_ORG_ROLE=Admin
      - GF_AUTH_DISABLE_LOGIN_FORM=true
    volumes:
      - ./config/grafana-datasources.yaml:/etc/grafana/provisioning/datasources/datasources.yaml
      - grafana-data:/var/lib/grafana
    ports:
      - "3000:3000"
```

2. Configure its three data sources using the configuration file provided to the container with volumes.

```yaml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
    version: 1
    editable: false
    isDefault: false
  
  - name: Loki
    type: loki
    access: proxy
    url: http://loki:3100
    version: 1
    editable: false
    isDefault: true

  - name: Jaeger
    type: jaeger
    access: proxy
    url: http://jaeger:16686
    version: 1
    editable: false
    isDefault: false
```

## Trying out the system

1. Compile the services and run the ecosystem

```bash
$ mvn clean package -Dmaven.test.skip=true
$ docker compose build
$ docker compose up --detach
```

2. Install explore plugins in Grafana (execute the following command inside the running container)

```bash
$ grafana cli --pluginUrl=https://storage.googleapis.com/integration-artifacts/grafana-lokiexplore-app/grafana-lokiexplore-app-latest.zip plugins install grafana-lokiexplore-app

# This requires Tempo, do not work with Jaeger
$ grafana cli --pluginUrl=https://storage.googleapis.com/integration-artifacts/grafana-exploretraces-app/grafana-exploretraces-app-latest.zip plugins install grafana-traces-app
```

3. Connect to [localhost:3000](http://localhost:3000) to start exploring

* In Explore -> Metrics you can see a graphical representation of all collected metrics
* In Explore -> Logs you can see all collected logs
* In Explore -> Select **Jaeger** as datasource, **Search** as Query type. Select a **service name** and an **operation name** to see the traces.
* In Dashboards -> New -> Import, paste https://grafana.com/grafana/dashboards/19004-spring-boot-statistics/ and enjoy your dashboard.





## Resources