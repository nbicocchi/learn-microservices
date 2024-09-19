# Metrics Aggregation

Every application that is deployed on production needs some kind of monitoring to see how the application is performing. This will give you some insights on whether the application is performing as expected or if you would need to take some action in order to obtain the desired level of performance. This data is called Application Performance Metrics (APM). There are several tools available:
* [Prometheus](https://prometheus.io/)
* [Grafana](https://grafana.com/)
* [Newrelic](https://newrelic.com/)
* [Datadog APM](https://www.datadoghq.com/product/apm/)

Prometheus is a popular open-source monitoring and alerting system. It collects, stores, and analyzes metrics from various sources like applications, services, operating systems, and hardware devices. It offers insights into performance and system health.

Grafana is an open-source tool for data visualization, monitoring, and troubleshooting. It creates dashboards to visualize metrics from sources like Prometheus, Elasticsearch, InfluxDB, and CloudWatch. You can customize the dashboards with graphs, tables, charts, and maps to suit your needs.

![](https://miro.medium.com/v2/resize:fit:1400/1*zg4Et9531n1MgRkeESif1w.png)

## Maven dependencies
To enable APM, add to *ALL* services within our ecosystem the *spring-boot-starter-actuator*, and *micrometer-registry-prometheus* dependencies.

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
    <scope>runtime</scope>
</dependency>
```

In particular:
* *spring-boot-starter-actuator* dependency enables endpoints that expose information about your application’s health, metrics, configuration, and monitoring.
* *micrometer-registry-prometheus* Micrometer dependency is used to configure Spring Boot applications to expose metrics in a format that Prometheus can scrape and store.

## Services configuration

As above, *ALL* services within our ecosystem can be configured to expose metrics in the proper way:

```
management.endpoint.health.show-details: "ALWAYS"
management.endpoints.web.exposure.include: "*"
```

This specifies:
* that all actuator endpoints will be exposed (including *prometheus*)
* that the *health* endpoint will show additional details

For testing, you can execute Eureka and TimeService and call:

* [http://localhost:9001/actuator](http://localhost:9001/actuator) for showing the list of all the available actuator endpoints.

```
{
    "_links": {
    "self": {
    "href": "http://localhost:9001/actuator",
    "templated": false
},
    "beans": {
    "href": "http://localhost:9001/actuator/beans",
    "templated": false
},
    "caches-cache": {
    "href": "http://localhost:9001/actuator/caches/{cache}",
    "templated": true
},
    "caches": {
    "href": "http://localhost:9001/actuator/caches",
    "templated": false
},
...
```

* [http://localhost:9001/actuator/prometheus](http://localhost:9001/actuator/prometheus) for showing all prometheus metrics.

```
# HELP jvm_buffer_count_buffers An estimate of the number of buffers in the pool
# TYPE jvm_buffer_count_buffers gauge
jvm_buffer_count_buffers{id="mapped - 'non-volatile memory'",} 0.0
jvm_buffer_count_buffers{id="mapped",} 0.0
jvm_buffer_count_buffers{id="direct",} 23.0
# HELP jvm_threads_live_threads The current number of live threads including both daemon and non-daemon threads
# TYPE jvm_threads_live_threads gauge
jvm_threads_live_threads 29.0
# HELP system_cpu_count The number of processors available to the Java virtual machine
# TYPE system_cpu_count gauge
system_cpu_count 8.0
# HELP http_server_requests_active_seconds_max  
# TYPE http_server_requests_active_seconds_max gauge
http_server_requests_active_seconds_max{exception="none",method="GET",outcome="SUCCESS",status="200",uri="UNKNOWN",} 0.001578521
# HELP http_server_requests_active_seconds  
# TYPE http_server_requests_active_seconds summary
...
```

## Prometheus Configuration

To start Prometheus, we will be using a Prometheus docker image and provide it with some configuration to gather the metrics data from our application. It does so by creating jobs that will scrape data from an endpoint. So let’s define the job in the `prometheus.yaml` configuration file as below.

```yaml
scrape_configs:
  - job_name: 'Spring Boot Application input'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 2s
    static_configs:
      - targets: ['time:8080', 'composite:8080', 'gateway:8080', 'eureka:8761']
        labels:
          application: "My Spring Boot Application"
```

We defined a job that will call the actuator endpoint on our application every 2 seconds to get the metrics data.


## Docker configuration

```yaml
services:

  prometheus:
    image: prom/prometheus:v2.35.0
    volumes:
      - ./data/prometheus/config:/etc/prometheus/
    command:
      - "--config.file=/etc/prometheus/prometheus.yml"
    ports:
      - 9090:9090

  grafana:
    image: grafana/grafana-oss:8.5.2
    user: root
    volumes:
      - ./data/grafana:/var/lib/grafana
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
      - GF_USERS_ALLOW_SIGN_UP=false
      - GF_SERVER_DOMAIN=localhost
      # Enabled for logging
      - GF_LOG_MODE=console file
      - GF_LOG_FILTERS=alerting.notifier.slack:debug alertmanager:debug ngalert:debug
    ports:
      - 3000:3000

  eureka:
    build: eureka-server-end
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  gateway:
    build: gateway-service-end
    mem_limit: 512m
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  composite:
    build: composite-service-end
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  time:
    build: time-service-end
    mem_limit: 512m
    environment:
      - SPRING_PROFILES_ACTIVE=docker
```

Here, we have the config file mounted at the location */etc/prometheus* and we use the location of the config file as an argument to the command. Start the microservice ecosystem with:

```
$ docker compose up -d
```

Then, open http://localhost:9090 on and search for the label `logback_events_total` as shown below.

![](images/metrics-prometheus-intro.avif)

## Visualizing Metrics in Grafana

### Add the Prometheus data source
Open http://localhost:3000 and access Grafana using the username and password *admin*.

Navigate to *Add your first data source* and select *Prometheus*. Then you need to only add a single property i.e., the Prometheus URL http://prometheus:9090.

![](images/metrics-grafana-add-datasource.avif)

### Creating a dashboard

Click on the *+* icon on the left and then select *Create Dashboard* and *Add new panel*.

Next, let's query for a label in the metric browser i.e., *logback_events_total*

![](images/metrics-grafana-first-dashboard.avif)

As you can see here, we get counts of all types of logs. These counts are currently from our application’s startup logs and are shown in a time-series format.

Let’s drill down to only view the warning logs. For this, we would have to add the attribute *level="warn"* as below.

![](images/metrics-grafana-warn.avif)

We just created a simple metric visualization panel to view the number of warning logs. Usually, we would like to view the rate of errors or warning logs over a certain period of time. This will help us to understand if there is some problem in our system. For this, we can use the *rate* function to calculate the rate of logs over a particular period of time.

![](images/metrics-grafana-warn-rate.avif)

Now, we don’t need to create dashboards from scratch. Rather there are quite many community-provided dashboards. You can find one inside *data/grafana-dashboard*.

![](images/metrics-grafana-community-dashboard.avif)

## Reference