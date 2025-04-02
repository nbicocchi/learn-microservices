# Observability (OTLP)

## Services instrumentation

In this lab, we use a Java Agent providing zero-code instrumentation based on OpenTelemetry.

1. Download the [opentelemetry-java-instrumentation](https://github.com/open-telemetry/opentelemetry-java-instrumentation) library and place the jar file inside the service source folder.

2. Modify the Dockerfile to automatically run the Java Agent with the service.

```dockerfile
FROM eclipse-temurin:21
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
COPY opentelemetry-javaagent.jar opentelemetry-javaagent.jar
ENV JAVA_TOOL_OPTIONS="-javaagent:/opentelemetry-javaagent.jar"
ENTRYPOINT ["java","-jar","/application.jar"]
```

3. Using environment variables, instruct each service to send their full telemetry to a specific [Open Telemetry Collector](https://opentelemetry.io/docs/collector/).

```yaml
  eureka:
    build: eureka-service
    ports:
      - "8761:8761"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318
    healthcheck:
      test: [ "CMD-SHELL", "curl -f http://localhost:8761" ]
      interval: 10s
      timeout: 5s
      retries: 5

  datetime-composite:
    build: datetime-composite-service
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318
    healthcheck:
      test: [ "CMD-SHELL", "curl -f http://localhost:8080/actuator/health" ]
      interval: 10s
      timeout: 5s
      retries: 5

  datetime:
    build: datetime-service
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - OTEL_EXPORTER_OTLP_ENDPOINT=http://otel-collector:4318
    healthcheck:
      test: [ "CMD-SHELL", "curl -f http://localhost:8080/actuator/health" ]
      interval: 10s
      timeout: 5s
      retries: 5
```

## Open Telemetry Collector

1. Add the Open Telemetry Collector to our service ecosystem.

```yaml
  otel-collector:
    image: otel/opentelemetry-collector-contrib:latest
    volumes:
      - ./config/otel-collector-config.yaml:/etc/otel-collector-config.yaml
    command: --config /etc/otel-collector-config.yaml
    depends_on:
      - jaeger
```

2. Configure the collector using the configuration file provided. Please note that with this setup metrics are actually discarded and their collection is delegated to Prometheus.

```yaml
receivers:
  otlp:
    protocols:
      grpc:
        endpoint: "0.0.0.0:4317"
      http:
        endpoint: "0.0.0.0:4318"

processors:
  batch:

connectors:
  spanmetrics:

exporters:
  debug:
    verbosity: detailed

  prometheus:
    endpoint: 0.0.0.0:8889

  #prometheusremotewrite:
  #  endpoint: http://prometheus:9090/api/v1/write

  otlphttp:
    endpoint: http://loki:3100/otlp

  otlp/jaeger:
    endpoint: jaeger:4317
    tls:
      insecure: true

service:
  telemetry:
    metrics:
      readers:
        - pull:
            exporter:
              prometheus:
                host: '0.0.0.0'
                port: 8888

  pipelines:
    logs:
      receivers: [ otlp ]
      exporters: [ otlphttp ]

    traces:
      receivers: [otlp]
      exporters: [otlp/jaeger, spanmetrics]

    # The metrics pipeline receives generated span metrics from 'spanmetrics' connector
    # and pushes to Prometheus exporter, which makes them available for scraping on :8889.
    metrics/spanmetrics:
      receivers: [spanmetrics]
      exporters: [prometheus]
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

1. Add the Jaeger backend to the service ecosystem. The default configuration is OK.

```yaml
  jaeger:
    image: jaegertracing/all-in-one:latest
    environment:
      - METRICS_STORAGE_TYPE=prometheus
    ports:
      - "16686:16686"
```




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

2. Configure its three data sources using the configuration file provided:

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
mvn clean package -Dmaven.test.skip=true
docker compose up --build --detach
```

2. Install Loki plugin in Grafana (execute the following command inside the running container from Docker Desktop)

```bash
$ grafana cli --pluginUrl=https://storage.googleapis.com/integration-artifacts/grafana-lokiexplore-app/grafana-lokiexplore-app-latest.zip plugins install grafana-lokiexplore-app
```

2. Connect to [localhost:3000](http://localhost:3000) to start exploring

* Home -> Data sources -> Check that Jaeger, Loki, Prometheus are set
* Home -> DrillDown -> Metrics
* Home -> DrillDown -> Logs
* Home -> Explore -> Select **Jaeger** as datasource, **Search** as Query type. Select a **service name** and an **operation name** to see the traces.
* Dashboards -> New -> Import, paste:
  * https://grafana.com/grafana/dashboards/19004-spring-boot-statistics/
  * https://grafana.com/grafana/dashboards/21308-http/
  * https://grafana.com/grafana/dashboards/22108-jvm-springboot3-dashboard-for-prometheus-operator/
  * https://github.com/resilience4j/resilience4j/blob/master/grafana_dashboard.json

## Resources