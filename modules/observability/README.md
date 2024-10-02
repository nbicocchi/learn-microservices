# Labs

## Lab 1: Setting Up Prometheus for Metrics Collection
**Objective:** Install and configure Prometheus to collect metrics from a Spring Boot application.

**Instructions:**
- Create a simple Spring Boot application with a RESTful API that provides some basic metrics (e.g., request count, response time).
- Add the `micrometer-core` and `spring-boot-starter-actuator` dependencies to the application.
- Configure Prometheus to scrape metrics from the Spring Boot application by adding the appropriate configuration to the `application.yml` file.
- Run Prometheus and verify that it collects metrics from the Spring Boot application.

**Expected Outcomes:**
- Students will understand how to set up Prometheus for metrics collection in a Spring Boot application.
- They will gain hands-on experience with exposing application metrics and configuring Prometheus.

## Lab 2: Visualizing Metrics with Grafana
**Objective:** Use Grafana to create dashboards for visualizing metrics collected by Prometheus.

**Instructions:**
- Install Grafana and configure it to use Prometheus as a data source.
- Create a new dashboard in Grafana to visualize key metrics from the Spring Boot application (e.g., request rate, error rate, response time).
- Add various visualization panels (e.g., graphs, tables) to the dashboard and customize them for better insights.
- Set up alerts in Grafana based on specific metrics thresholds (e.g., alert on high response times).

**Expected Outcomes:**
- Students will learn how to visualize application metrics using Grafana.
- They will understand the importance of monitoring and alerting for maintaining application health.

## Lab 3: Implementing the ELK Stack for Log Management
**Objective:** Set up the ELK stack to collect and analyze logs from a Spring Boot application.

**Instructions:**
- Create a Spring Boot application that generates log entries at different log levels (INFO, WARN, ERROR).
- Install and configure Elasticsearch, Logstash, and Kibana to form the ELK stack.
- Use Logstash to collect logs from the Spring Boot application and send them to Elasticsearch.
- Create visualizations in Kibana to analyze log data and create a dashboard that shows log trends and error rates.

**Expected Outcomes:**
- Students will understand how to implement the ELK stack for effective log management and analysis.
- They will gain experience in collecting, storing, and visualizing logs to enhance observability.

# Questions
1. What is observability, and why is it important in microservices architectures?
2. Explain the role of metrics in observability and how they differ from logs and traces.
3. What is Prometheus, and how does it facilitate metrics collection in applications?
4. Describe the process of exposing application metrics using Micrometer and Spring Boot Actuator.
5. How do you configure Prometheus to scrape metrics from a Spring Boot application?
6. Discuss the benefits of using Grafana for visualizing metrics collected by Prometheus.
7. What are the key components of the ELK stack, and how do they work together for log management?
8. Explain how Logstash is used to process logs before sending them to Elasticsearch.
9. What is the significance of Kibana in the ELK stack, and how does it enhance log analysis?
10. Discuss best practices for implementing observability in microservices, focusing on metrics, logs, and traces.
