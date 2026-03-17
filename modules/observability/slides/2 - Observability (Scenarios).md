# Observability (Scenarios)

### CPU Metrics

* **`jvm_cpu_recent_utilization_ratio`** – Recent CPU usage of the JVM.

  * Low values → system lightly loaded.
  * High values → high CPU utilization or potential bottlenecks.

* **`jvm_thread_count`** – Total number of active threads in the JVM.

  * Can indicate thread contention, leaks, or high concurrency.

* **Thread states** – Breakdown of threads by state (e.g., `waiting`, `runnable`, `timed_waiting`).

  * Helps understand how threads are spending their time and identify bottlenecks.

### HTTP Server Metrics

* **`http_server_request_duration_seconds_bucket`** – Histogram of server-side request durations.

  * Shows the number of requests completed within specific time intervals (buckets).
  * Useful for latency distributions and detecting slowdowns.

* **`http_server_request_duration_seconds_count`** – Total cumulative number of server requests handled.

  * Helps verify throughput and detect dropped or blocked requests.

* **`http_server_request_duration_seconds_sum`** – Total cumulative time spent handling HTTP requests.

  * Combined with `_count` to calculate average latency:

    ```
    average_latency = _sum / _count
    ```
  * Highlights periods of high processing times.

* **HTTP status codes** – Tracks counts of response codes (200, 500, 503, etc.).

  * Useful to identify failed or timed-out requests.

### HTTP Client Metrics

* **`http_client_request_duration_seconds_bucket`** – Histogram of client-side request durations.

  * Shows the latency experienced by the client for each request.
  * Helps detect slow external calls or network issues.

* **`http_client_request_duration_seconds_count`** – Cumulative count of requests made by the client.

* **`http_client_request_duration_seconds_sum`** – Total time spent by the client waiting for responses.

  * Useful for calculating average client latency.

### Other Metrics / Logs / Traces

* **Traces (Tempo)** – Distributed traces for each request, showing service interactions, errors, and timing.

  * Helps pinpoint where failures or delays occur in the request path.

* **Logs (Loki)** – Application and service logs for debugging failures, errors, or unexpected behavior.

These metrics form the foundation for observing system performance under different load conditions, helping to detect CPU/memory pressure, thread contention, request slowdowns, errors, and timeouts.

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

