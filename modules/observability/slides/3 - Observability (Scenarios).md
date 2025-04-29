# Observability (Scenarios)

## Low load

```bash
echo 'GET http://127.0.0.1:8080/divisors?n=18&times=100&faults=0' | vegeta attack -duration=300s --rate=5 | vegeta report
```
```
jvm_cpu_recent_utilization_ratio -> low
jvm_thread_count -> low
http_client_request_duration_seconds_bucket -> low latency
http_server_request_duration_seconds_bucket -> low latency
http://localhost:8080/actuator/circuitbreakers -> closed
logs, traces -> no red
```

## Mid load

```bash
echo 'GET http://127.0.0.1:8080/divisors?n=18&times=10000000&faults=0' | vegeta attack -duration=300s --rate=5 | vegeta report
```
```
jvm_cpu_recent_utilization_ratio -> higher
jvm_thread_count -> higher (math service)
http_client_request_duration_seconds_bucket -> higher
http_server_request_duration_seconds_bucket -> higher
http://localhost:8080/actuator/circuitbreakers -> closed
logs, traces -> no red
```

## High load

```bash
echo 'GET http://127.0.0.1:8080/divisors?n=18&times=100000000&faults=0' | vegeta attack -duration=300s --rate=5 | vegeta report
```
```
jvm_cpu_recent_utilization_ratio -> higher
jvm_thread_count -> higher (gateway service)
http_client_request_duration_seconds_bucket -> higher
http_server_request_duration_seconds_bucket -> higher
http://localhost:8080/actuator/circuitbreakers -> closed
logs, traces -> no red
```

## Faults

```bash
echo 'GET http://127.0.0.1:8080/divisors?n=18&times=100&faults=90' | vegeta attack -duration=300s --rate=5 | vegeta report
```
```
jvm_cpu_recent_utilization_ratio -> low
jvm_thread_count -> low
http_client_request_duration_seconds_bucket -> low
http_server_request_duration_seconds_bucket -> low
http://localhost:8080/actuator/circuitbreakers -> open
logs, traces -> red with empty space (when circuit breakers open)
```
