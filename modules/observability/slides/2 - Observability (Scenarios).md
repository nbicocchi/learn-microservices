# Observability (Scenarios)

### Key Metrics

| Metric                                        | Type      | Description                               |
| --------------------------------------------- | --------- | ----------------------------------------- |
| `jvm_cpu_recent_utilization_ratio`            | Gauge     | Recent CPU usage by JVM                   |
| `jvm_thread_count`                            | Gauge     | Number of active JVM threads              |
| `jvm_memory_used_bytes`                       | Gauge     | Memory currently used by the JVM          |
| `jvm_gc_collection_seconds_total`             | Counter   | Total time spent in garbage collection    |
| `http_requests_total`                         | Counter   | Total number of HTTP requests received    |
| `http_server_request_duration_seconds_bucket` | Histogram | Duration of HTTP server requests          |
| `http_client_request_duration_seconds_bucket` | Histogram | Duration of outgoing HTTP client requests |
| `http_errors_total`                           | Counter   | Total number of HTTP errors (4xx, 5xx)    |
| `process_resident_memory_bytes`               | Gauge     | Resident memory used by the process       |
| `node_memory_MemAvailable_bytes`              | Gauge     | Available memory on the node              |


## Scenarios

**🟢 Low Load**

* **Vegeta command:**

```
echo 'GET http://127.0.0.1:8080/mcd?a=18&b=12&times=100&faults=0' | vegeta attack -duration=60s --rate=10 | vegeta report
```

* **Metrics to observe:**

   * `jvm_cpu_recent_utilization_ratio`: low CPU usage (max ~0.0175)
   * `jvm_thread_count`: low thread count
   * `http_server_request_duration_seconds_bucket`: stable, low latency (~3.25 ms peak)
   * `http_client_request_duration_seconds_bucket`: stable, low latency (2–6 ms)

---

**🟡 Mid Load**

* **Vegeta command:**

```
echo 'GET http://127.0.0.1:8080/mcd?a=18&b=12&times=100000&faults=0' | vegeta attack -duration=60s --rate=20 -timeout=120s | vegeta report
```

* **Metrics to observe:**

   * `jvm_cpu_recent_utilization_ratio`: higher CPU usage (peak ~0.118)
   * `jvm_thread_count`: increased threads, mostly waiting/runnable
   * `http_server_request_duration_seconds_bucket`: gateway and math-service show slowdowns, some 503 errors
   * `http_client_request_duration_seconds_bucket`: increased latency, some request timeouts

---

**🔴 High Load**

* **Vegeta command:**

```
echo 'GET http://127.0.0.1:8080/mcd?a=18&b=12&times=1000000&faults=0' | vegeta attack -duration=180s --rate=60 -timeout=120s | vegeta report
```

* **Metrics to observe:**

   * `jvm_cpu_recent_utilization_ratio`: high CPU usage
   * `jvm_thread_count`: high, thread contention visible
   * `http_server_request_duration_seconds_bucket`: almost all requests fail or timeout
   * `http_client_request_duration_seconds_bucket`: extreme latency, 0% success

---

**⚠ Mid Load with Faults (40%)**

* **Vegeta command:**

```
echo 'GET http://127.0.0.1:8080/mcd?a=18&b=12&times=1000000&faults=40' | vegeta attack -duration=180s --rate=3 -timeout=120s | vegeta report
```

* **Metrics to observe:**

   * `jvm_cpu_recent_utilization_ratio`: moderate to high CPU usage
   * `jvm_thread_count`: elevated threads
   * `http_server_request_duration_seconds_bucket`: mixed success/failures (200, 500, 503)
   * `http_client_request_duration_seconds_bucket`: most requests fail or timeout

