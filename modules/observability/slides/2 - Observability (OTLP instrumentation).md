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
      - OTEL_METRIC_EXPORT_INTERVAL=1000
      - OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf
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
      - OTEL_METRIC_EXPORT_INTERVAL=1000
      - OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf
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
      - OTEL_METRIC_EXPORT_INTERVAL=1000
      - OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf
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

exporters:
  otlphttp:
    logs_endpoint: http://loki:3100/otlp/v1/logs
    metrics_endpoint: http://prometheus:9090/api/v1/otlp/v1/metrics
    traces_endpoint: http://tempo:4318/v1/traces

service:
  pipelines:
    metrics:
      receivers: [ otlp ]
      exporters: [ otlphttp ]

    logs:
      receivers: [ otlp ]
      exporters: [ otlphttp ]

    traces:
      receivers: [otlp]
      exporters: [otlphttp]
```

## Grafana backends

1. Add the Prometheus/Loki/Tempo backends and their configuration

```yaml
  prometheus:
    image: prom/prometheus:v3.2.1
    volumes:
      - ./config/prometheus.yaml:/etc/prometheus.yaml
    command:
      - --config.file=/etc/prometheus.yaml
      - --web.enable-otlp-receiver
      - --enable-feature=exemplar-storage
      - --enable-feature=native-histograms
    ports:
      - "9090:9090"
        
  loki:
    image: grafana/loki:latest
    command: [ "-config.file=/etc/loki/loki.yaml" ]
    volumes:
      - ./config/loki.yaml:/etc/loki/loki.yaml
  
  # Tempo runs as user 10001, and docker compose creates the volume as root.
  # As such, we need to chown the volume in order for Tempo to start correctly.
  init:
    image: &tempoImage grafana/tempo:latest
    user: root
    entrypoint:
      - "chown"
      - "10001:10001"
      - "/var/tempo"
    volumes:
      - ./tempo-data:/var/tempo

  memcached:
    image: memcached:latest
    container_name: memcached
    environment:
      - MEMCACHED_MAX_MEMORY=64m  # Set the maximum memory usage
      - MEMCACHED_THREADS=4       # Number of threads to use

  tempo:
    image: *tempoImage
    command: [ "-config.file=/etc/tempo.yaml" ]
    volumes:
      - ./config/tempo.yaml:/etc/tempo.yaml
      - tempo-data:/var/tempo
    depends_on:
      - init
      - memcached
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
    editable: true
    isDefault: true

  - name: Loki
    type: loki
    access: proxy
    url: http://loki:3100
    version: 1
    editable: true
    isDefault: false

  - name: Tempo
    type: tempo
    access: proxy
    url: http://tempo:3200
    version: 1
    editable: true
    isDefault: false
```

## Trying out the system

1. Compile the services and run the ecosystem

```bash
mvn clean package -Dmaven.test.skip=true
docker compose up --build --detach
```

2. Install DrillDown plugins in Grafana (execute the following commands inside the running container from Docker Desktop)

```bash
$ grafana cli --pluginUrl=https://storage.googleapis.com/integration-artifacts/grafana-lokiexplore-app/grafana-lokiexplore-app-latest.zip plugins install grafana-lokiexplore-app

$ grafana cli --pluginUrl=https://storage.googleapis.com/integration-artifacts/grafana-exploretraces-app/grafana-exploretraces-app-latest.zip plugins install grafana-traces-app
```

2. Connect to [localhost:3000](http://localhost:3000) to start exploring

* Home -> Data sources -> Check that Prometheus, Loki, Tempo are set
* Home -> DrillDown -> Metrics
* Home -> DrillDown -> Logs
* Home -> DrillDown -> Traces
* Dashboards -> New -> Import, paste:
  * https://grafana.com/grafana/dashboards/19004-spring-boot-statistics/
  * https://grafana.com/grafana/dashboards/21308-http/
  * https://grafana.com/grafana/dashboards/22108-jvm-springboot3-dashboard-for-prometheus-operator/
  * https://github.com/resilience4j/resilience4j/blob/master/grafana_dashboard.json

## Resources