# Labs

## Lab 1: Setting Up Prometheus for Metrics Collection
**Objective:** Install and configure Prometheus to collect metrics from a Spring Boot application.

**Instructions:**
- Create a simple Spring Boot application with a RESTful API that provides some basic metrics (e.g., request count, response time).
- Configure Prometheus to scrape metrics from the Spring Boot application by adding the appropriate configuration to the `application.yml` file.
- Run Prometheus and verify that it collects metrics from the Spring Boot application.

## Lab 2: Visualizing Metrics with Grafana
**Objective:** Use Grafana to create dashboards for visualizing metrics collected by Prometheus.

**Instructions:**
- Install Grafana and configure it to use Prometheus as a data source.
- Create a new dashboard in Grafana to visualize key metrics from the Spring Boot application (e.g., request rate, error rate, response time).
- Add various visualization panels (e.g., graphs, tables) to the dashboard and customize them for better insights.

## Lab 3: Collecting Logs with Logstash and Loki
**Objective:** Set up the ELK stack to collect and analyze logs from a Spring Boot application.

**Instructions:**
- Create a Spring Boot application that generates log entries at different log levels (INFO, WARN, ERROR).
- Install and configure Logstash, and Loki to collect logs.
- Create visualizations in Grafana to analyze log data and create a dashboard that shows log trends and error rates.

# Questions
1. What is observability, and why is it important in microservices architectures?
2. Explain the distinct role of metrics, logs, and traces in observability.
3. Signals, sources, agents, frontends, and backends are the key components of observability architectures. Describe their role and mutual interactions.
4. What is defined as telemetry in the context of observability? In this context, describe the role of OpenTelemetry and its advantages over previous approaches.
5. What is the *Business Logic to Instrumentation* ratio?
6. Describe the process of exposing application metrics using both Spring Boot Actuator and the Prometheus endpoint.
7. Java provides a form of automatic, zero-code instrumentation. Describe it, and its advantages compared to manual alternatives.
8. What is Prometheus, and how does it facilitate metrics collection in applications? Which is its primary approach to metrics collection? 
9. What is the Grafana stack? Describe its architecture and key components.
10. What is the ELK stack? Describe its architecture and key components.

