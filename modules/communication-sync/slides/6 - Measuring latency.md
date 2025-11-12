# Measuring latency

## Load Testing Overview

* Load testing measures service behavior under expected traffic.
* Key metrics:

  * **Latency** (response time)
  * **Throughput** (requests per second)
  * **Error rate**
* Tools by protocol:

  * REST: `hey`, `Locust`, `Vegeta`
  * gRPC: `ghz`, `grpcurl`
  * GraphQL: `Locust`
  * Flexible/custom: `Locust` (Python)

---

## hey – Simple REST Benchmark

* Lightweight CLI for HTTP load testing.
* Installation (Go):

```bash
go install github.com/rakyll/hey@latest
````

* Usage example:

```bash
hey -n 1000 -c 50 http://localhost:8080/products
```

* `-n 1000`: total requests
* `-c 50`: total threads

---

## ghz – gRPC Load Testing

* CLI and Go library for gRPC services.
* Installation (Go):

```bash
go install github.com/bojand/ghz/cmd/ghz@latest
```

* Usage example:

```bash
ghz --proto product.proto \
    --call ProductService.GetAllProducts \
    --in '{}' \
    -n 1000 \
    -c 50 \
    localhost:9090
```

---

## grpcurl – Interactive gRPC CLI

* Like `curl` for gRPC.
* Installation (Go):

```bash
go install github.com/fullstorydev/grpcurl/cmd/grpcurl@latest
```

* Usage example:

```bash
grpcurl -plaintext localhost:9090 list
grpcurl -plaintext localhost:9090 describe ProductService
grpcurl -plaintext -d '{}' localhost:9090 ProductService.GetAllProducts
```

---

## Locust – Customizable Load Testing

* Python-based, supports REST, GraphQL, WebSockets.
* Installation:

```bash
python3 -m venv venv
source venv/bin/activate
pip install locust requests
```

* Example for **REST**:

```python
from locust import HttpUser, task

class ProductUser(HttpUser):
    @task
    def get_products(self):
        self.client.get("/products")
```

* Example for **GraphQL**:

```python
from locust import HttpUser, task

class GraphQLUser(HttpUser):
    @task
    def get_products(self):
        query = '{"query": "{ products { uuid name weight } }"}'
        self.client.post("/graphql", data=query, headers={"Content-Type": "application/json"})
```

* Run:

```bash
locust -f locustfile.py
```

---

## Vegeta – HTTP Load Testing

* Flexible HTTP load testing tool for REST or any HTTP service.
* Can be used as CLI or library (Go).
* Installation (Go):

```bash
go install github.com/tsenart/vegeta@latest
```

* Usage example – **attack mode**:

```bash
echo "GET http://localhost:8080/products" | vegeta attack -duration=30s -rate=50 | vegeta report
```

* `-duration=30s`: total test duration

* `-rate=50`: requests per second

* Usage example – **generate report**:

```bash
echo "GET http://localhost:8080/products" | vegeta attack -duration=30s -rate=50 | vegeta report -type=text
```

* Can produce reports in **text, JSON, or plots**:

```bash
vegeta plot < results.bin > plot.html
```

* Can read a file of requests:

```bash
vegeta attack -targets=targets.txt -rate=100 -duration=1m | vegeta report
```

* Good for **scriptable, high-throughput HTTP testing**.

---

## Resources
