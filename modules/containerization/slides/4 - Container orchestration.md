# Docker Compose Overview

## Container Orchestration

Container orchestration is the **automated management** of containerized applications across multiple hosts. It ensures that containers are efficiently deployed, scaled, networked, and managed in a production environment.
* **Automated Deployment & Scheduling** – Ensures containers are placed on the right nodes.  
* **Scaling** – Dynamically adjusts the number of containers based on demand.  
* **Load Balancing** – Distributes traffic across containers efficiently.  
* **Self-Healing** – Automatically restarts failed containers or reschedules them.  
* **Service Discovery & Networking** – Manages inter-container communication.  
* **Security & Access Control** – Manages role-based access and secrets.

**Popular Container Orchestration Tools**

* Docker Compose (single-node)
* Docker Swarm (multi-node)
* Kubernetes (K8s)
* Red Hat OpenShift (K8s-based platform)

## The `docker-compose.yml` file
**Docker Compose is a powerful tool that allows us to define and manage multi-container Docker applications**. **Docker running a single node does not provide full orchestration features**. It is particularly useful when working with microservices ecosystems, as it enables the coordination of multiple containers. With Compose, we can configure networking, resources, and also address scalability requirements.

The `docker-compose.yaml` file follows a hierarchical structure by the use of indentations.

- **services**: Defines the containers of the application, each service represents a container.
  - **[service-name]**: Name of the single service, the choice is at our discretion.
    - **image**: Specifies the image to use for the service.
    - **build**: Alternative to `image`, allows to build an image from a Dockerfile.
    - **ports**: Maps the host's ports.
    - **volumes**: Allows to share data between container and host or between containers.
    - **networks**: Defines the networks which the containers will utilize to communicate.
    - **depends_on: `<service>`**: Defines dependencies between services (which service start first).
    - **environment**: Used to pass environment variables to configure containers and applications.
    - **healthcheck**: Ensures that the service is healthy, specifying the interval and number of tries.


## Command line utility

We manage containers (_services_) ecosystem thanks to `docker compose` command (or `docker-compose` in old versions).

First of all, we must build services in order to be able to make containers:

```bash
docker compose build
```

Then, we can start the ecosystem with the `docker compose up` command in the context of the project directory.

```bash
docker compose up --detach
```

> **[NOTE]** We use `detach` flag in order to run containers in the background and prevent verbose outputs.

Given that we will always copy _last built_ JAR file in our microservice container, we must **re-build** microservice **image every** time that we change something in code.

In practice, we can use these two commands:

```bash
mvn clean package -Dmaven.test.skip=true
docker compose -f <compose-configuration>.yml up --build --detach
```

Which is a short version of these:

```bash
export COMPOSE_FILE=<compose-configuration>.yaml
mvn clean package -Dmaven.test.skip=true
docker compose up --build --detach
```

## Simple example
Now we will deploy a simple application mapped to the port 5000. Below, the `docker-compose.yml` file.

```yaml
services:
  echo:
    build: .
    ports:
      - "5000:5000"
```

The application is a simple echo server written in Java with the capability of saving and retrieving logs (code/echo-server-logs-java). Its controller is reported below:

```java
@Log
@RestController
public class EchoController {
    @PostMapping(value = "/echo")
    public Map<String, Object> echo(@RequestBody String message) {
        log.info(message);
        return Map.of("echoed_data", message);
    }

    @GetMapping(value = "/logs")
    public Map<String, Object> logs() throws IOException {
        log.info("requested_logs");
        Path path = FileSystems.getDefault().getPath("/tmp", "application.log");
        return Map.of("lines", Files.readAllLines(path));
    }
}
```

```bash
export COMPOSE_FILE=docker-compose-simple.yml
mvn clean package -Dmaven.test.skip=true
docker compose up --build --detach
```

To receive an echoed message:

```bash
curl -X POST http://localhost:5000/echo -H "Content-Type: application/json" -d '{"message": "Hello, Echo Server!"}'
```

To see the logs:

```bash
curl -X GET http://localhost:5000/logs
```


## Resource limitation
In Docker Compose we can limit CPU and memory for containers (code/cpu-memory-meter). Its controller is reported below:

```java
@RestController
public class MeterController {

    @GetMapping(value = "/")
    public Map<String, Object> echo() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();

        return Map.of(
                "Available processors (cores)", Runtime.getRuntime().availableProcessors(),
                "Free memory (MB)", Runtime.getRuntime().freeMemory() / 1_000_000,
                "Maximum memory (MB)", maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory / 1_000_000,
                "Total memory (MB)", Runtime.getRuntime().totalMemory() / 1_000_000,
                "Allocated memory (MB)", (totalMemory - freeMemory) / 1_000_000
        );
    }

    @GetMapping(value = "/allocate/{size}")
    public Map<String, Object> allocate(@PathVariable Integer size) {
        try {
            byte[] array = new byte[size * 1_000_000];
            return Map.of("array allocation", "OK");
        } catch (OutOfMemoryError e) {
            return Map.of("array allocation", "Out of memory");
        }
    }
}
```

To apply the resource limitation to a specific `service` we need to add the `resources.limits` attribute under `deploy`.

```yaml
services:
  meter:
    build: .
    ports:
      - "8080:8080"
    deploy:
      resources:
        limits:
          memory: 512M    # Restrict memory to 512MB
          cpus: '2'       # Restrict cpus to 2 cores
```

```bash
unset COMPOSE_FILE
mvn clean package -Dmaven.test.skip=true
docker compose up --build --detach
```

```bash
curl http://localhost:8080 | jq
```

```json
{
  "Total memory (MB)": 37,
  "Maximum memory (MB)": 129,
  "Available processors (cores)": 2,
  "Allocated memory (MB)": 21,
  "Free memory (MB)": 16
}
```

As expected the number of (perceived) cores is 2. Maximum memory (MB) represents the **estimated** maximum size of the HEAP memory. The JVM usually sets it between 25pc and 50pc of the total memory. In this case 129MB is approximately 25pc of the total 512MB.

As a final test, you can try to allocate memory inside the service. This example allocates 20MB and works fine.

```bash
curl http://localhost:8080/allocate/20
```

```json
{"array allocation":"OK"}
```

Instead, this example allocates 200MB and produces an error.

```bash
curl http://localhost:8080/allocate/200
```

```json
{"array allocation":"Out of memory"} 
```

## Replicas
A **Replica** in Docker refers to the ability to instantiate more instances of the same container, this allows us to scale it horizontally (code/echo-server-logs-java).
The `deploy` field allows us to manage replicas and is composed of:
  - `mode`: **global** or **replicated**
  - `replicas`: the number of replicas.

> **NOTE:** If not specified, `mode` = `replicated` and `replicas` = `1`

```yaml
services:
  echo:
    build: .
    ports:
      - "5000"
    deploy:
      mode: replicated
      replicas: 3
```
> **NOTE:** Since the service we're replicating is mapping ports, we will specify only the container port (omitting the host port), otherwise it will show an error.

With the same procedure seen above, we start up the ecosystem.

```bash
export COMPOSE_FILE=docker-compose-replicas.yaml
mvn clean package -Dmaven.test.skip=true
docker compose up --build --detach
```

```bash
$ docker ps

CONTAINER ID   IMAGE        COMMAND         ...    PORTS
abc123         echo-server-logs-java-echo    "java -jar /applicat" ...    0.0.0.0:32768->5000/tcp
def456         echo-server-logs-java-echo    "java -jar /applicat" ...    0.0.0.0:32769->5000/tcp
ghi789         echo-server-logs-java-echo    "java -jar /applicat" ...    0.0.0.0:32770->5000/tcp
```

We can invoke one replica with:

```bash
curl -X POST http://localhost:32768/echo -H "Content-Type: application/json" -d '{"message": "Hello, Echo Server!"}'
```

## External Volumes
Docker containers allow us to **maintain persistent data surviving beyond the container lifecycle**, facilitating sharing data between containers, backup and restore. If a service doesn't specify a volumes section, **no data will be persisted outside the container**.

In the following example (code/echo-server-logs-java) we have an echo server configured to save logs in '/tmp/application.log'. 

```java
@Log
@RestController
public class EchoController {
    @PostMapping(value = "/echo")
    public Map<String, Object> echo(@RequestBody String message) {
        log.info(message);
        return Map.of("echoed_data", message);
    }

    @GetMapping(value = "/logs")
    public Map<String, Object> logs() throws IOException {
        log.info("requested_logs");
        Path path = FileSystems.getDefault().getPath("/tmp", "application.log");
        return Map.of("lines", Files.readAllLines(path));
    }
}
```

We can stop the container, restart the container and see that the previous logs disappeared (watch the timestamp!).

```bash
$ export COMPOSE_FILE=docker-compose-simple.yaml
$ mvn clean package -Dmaven.test.skip=true
$ docker compose up --build --detach
$ curl -X GET http://localhost:5000/logs | jq

{
  "lines": [
    "25-02-2025 19:07:16.968 [http-nio-5000-exec-1] INFO  c.n.echo.controller.EchoController.logs - requested_logs",
  ]
}

$ docker compose down
$ docker compose up --detach
$ curl -X GET http://localhost:5000/logs | jq
{
  "lines": [
    "25-02-2025 19:08:33.875 [http-nio-5000-exec-1] INFO  c.n.echo.controller.EchoController.logs - requested_logs"
  ]
}
```

Instead of saving `application.log` inside the container, it can be externalized in three different ways.

### Named Volume

- **Definition:** Defined and named manually by the user
- **Persistence:** Persistent, exists independently of the container
- **Access from Host:** Accessible via Docker commands
- **Typical Usage:** Persistent data (e.g., databases)
- **Security:** Secure, Docker-managed

```yaml
services:
  echo:
    build: .
    ports:
      - "5000:5000"
    volumes:
      - tmp:/tmp

volumes:
  tmp:
```

- **services**: Defines the services (containers) to be created. Here, only one service named `echo` is defined.
- **build**: Instructs Docker to build the image using a Dockerfile located in the current dir.
- **ports**: Maps a port on the host (left side) to a port on the container (right side). Here, both are set to `5000`.
- **volumes**: Specifies volumes to be mounted inside the container. The `tmp` volume is mounted at `/tmp` within the container, which could be used for persistent storage.
- **volumes** section: Defines the `tmp` volume, which persists data independent of the container lifecycle.

```bash
export COMPOSE_FILE=docker-compose-volume.yaml
mvn clean package -Dmaven.test.skip=true
docker compose up --build --detach
```

### Anonymous Volume

- **Definition:** Automatically created by Docker without a name
- **Persistence:** Temporary, removed with the container
- **Access from Host:** Not directly accessible
- **Typical Usage:** Temporary or transient data (temporary files, logs, writable storage for read-only containers)
- **Security:** Secure, Docker-managed

```yaml
services:
  echo:
    build: .
    ports:
      - "5000:5000"
    volumes:
      - /tmp
```

- **volumes**: The path `/tmp` refers to an anonymous volume. Docker will automatically create a volume without a specific name, and it will be mounted to the `/tmp` directory inside the container. This volume is used for storing data but won't have a persistent identity unless managed externally (i.e., you won't be able to reuse or easily reference this volume by name later).

```bash
export COMPOSE_FILE=docker-compose-anonvolume.yaml
mvn clean package -Dmaven.test.skip=true
docker compose up --build --detach
```

### Bind mount

- **Definition:** Links a host directory/file to a container path
- **Persistence:** Depends on the host filesystem
- **Access from Host:** Direct access from the host
- **Typical Usage:** Sync files during development
- **Security:** Full host access

```yaml
services:
  echo:
    build: .
    ports:
      - "5000:5000"
    volumes:
      - ./data:/tmp
```

- **volumes**: The path `./data:/tmp` specifies a bind mount. This means that the directory `./data` on the host machine is directly mounted into the container at `/tmp`. Any changes made in the container at `/tmp` will be reflected on the host's `./data` directory and vice versa.

```bash
mkdir data
export COMPOSE_FILE=docker-compose-bind.yaml
mvn clean package -Dmaven.test.skip=true
docker compose up --build --detach
```

## Networks
One aspect that makes the Docker engine a powerful tool is the **possibility of creating and managing the services' connectivity**. When using Docker Compose, networks are automatically created for your services, but you can also define custom networks to **control which services can communicate with each other**.

To list the networks created by Docker, you can run:

```bash
docker network ls
```

To inspect a specific network and see its details, including the associated containers, use:

```bash
docker network inspect <network_name>
```

Docker supports different types of networks:
- **Bridge**: The default network type for Docker containers. It isolates containers from the host network but allows communication between containers on the same bridge network.
- **Host**: Uses the host's networking stack directly. Containers share the host's network interfaces, which can lead to performance improvements but less isolation.
- **Overlay**: Allows containers to communicate across different Docker hosts. It's commonly used in swarm mode to manage clusters of containers.
- **Macvlan**: Assigns a MAC address to a container, allowing it to appear as a physical device on the network. Useful for applications that require direct access to the physical network.

### Interaction with `iptables`

Docker manages networking using `iptables`, a Linux utility for configuring network packet filtering rules:
* When you create a network (either by running `docker-compose up` or manually), Docker automatically adds `iptables` rules to allow traffic between containers in the same network.
* Each bridge network is assigned a subnet, and `iptables` rules are configured to allow communication between all containers on that subnet.
* You can inspect the `iptables` rules applied by Docker using `sudo iptables -L -n`. This command lists all the rules, including those created by Docker. Look for chains like `DOCKER` that contain rules specific to Docker containers.

### First example
In this example (code/network-example) we define the `echo` and `postgres` services connected to a custom network called `my_network`, using a `bridge` driver, which is the default for single-host setups.

```yaml
services:
  echo:
    build: echo-server-logs-db-java
    ports:
      - "5000:5000"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    networks:
      - my_network
    depends_on:
      postgres:
        condition: service_healthy

  postgres:
    image: postgres:latest
    environment:
      POSTGRES_USER: user
      POSTGRES_PASSWORD: secret
      POSTGRES_DB: jdbc_schema
    volumes:
      - pg-data:/var/lib/postgresql/data
    networks:
      - my_network
    healthcheck:
      test: [ "CMD-SHELL", "pg_isready -U user -d jdbc_schema" ]
      interval: 30s
      timeout: 10s
      retries: 5

networks:
  my_network:
    driver: bridge

volumes:
  pg-data:
```

```bash
unset COMPOSE_FILE
cd echo-server-logs-db-java
mvn clean package -Dmaven.test.skip=true
cd ..
docker compose up --build --detach
```

### Second example
A good way to use networks is for isolating a portion of the microservice ecosystem that does not need to be exposed to external networks. In this example (code/network-example), we define two networks `front_net` and `back_net`:
- The `frontend` and `echo` services share the `front_net`, allowing them to communicate.
- The `echo` and `database` services share the `back_net`, isolating database traffic from the frontend.
- `nginx` is used as a reverse proxy for API requests forwarding

![](images/ecosystem.webp)

```yaml
services:
  frontend:
    image: nginx
    volumes:
      - ./frontend/nginx.conf:/etc/nginx/nginx.conf  # Custom NGINX config
    networks:
      - front_net
    ports:
      - "8080:8080"
    depends_on:
      echo:
        condition: service_healthy

  echo:
    build: echo-server-logs-db-java
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    networks:
      - front_net
      - back_net
    depends_on:
      postgres:
        condition: service_healthy
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:5000/logs"]
      interval: 10s
      timeout: 5s
      retries: 5

  postgres:
    image: postgres:latest
    environment:
      POSTGRES_USER: user
      POSTGRES_PASSWORD: secret
      POSTGRES_DB: jdbc_schema
    volumes:
      - pg-data:/var/lib/postgresql/data
    networks:
      - back_net
    healthcheck:
      test: [ "CMD-SHELL", "pg_isready -U user -d jdbc_schema" ]
      interval: 10s
      timeout: 5s
      retries: 5

networks:
  front_net:
    driver: bridge
  back_net:
    driver: bridge

volumes:
  pg-data:
```

```bash
export COMPOSE_FILE=docker-compose-isolation.yaml
cd echo-server-logs-db-java
mvn clean package -Dmaven.test.skip=true
cd ..
docker compose up --build --detach
```

## Heartbeats

Heartbeat functions — commonly referred to as **health checks** — are mechanisms that periodically verify whether a service (container) is healthy and functioning as expected. Implementing heartbeat functions ensures that your application components are up and running, and allows Docker Compose to manage:

* **dependencies**: a service needs another service to run properly.
* **restarts**: a failing service can be restarted by Docker.

### Implementing Health Checks Using `curl`

> **NOTE:** curl must be available inside the container

```yaml
services:
  webapp:
    image: your-webapp-image
    ports:
      - "8080:8080"
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:8080/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

**Explanation:**

- **`test`:** Uses `curl` with the `-f` flag to fail on HTTP errors. It requests the `/health` endpoint.
- **`interval`:** Time between health checks (30 seconds).
- **`timeout`:** Maximum time to wait for a response (10 seconds).
- **`retries`:** Number of consecutive failures needed to mark the container as `unhealthy` (3 retries).

### Implementing Health Checks Using `wget`

> **NOTE:** wget must be available inside the container

Alternatively, you can use `wget` for the health check:

```yaml
services:
  webapp:
    image: your-webapp-image
    ports:
      - "8080:8080"
    healthcheck:
      test: ["CMD-SHELL", "wget --spider http://localhost:8080/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```


### Implementing Health Checks Using Special-Purpose Commands

For services like databases, specialized commands can provide more accurate health checks by verifying service-specific readiness.

```yaml
services:
  db:
    image: postgres:latest
    environment:
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
      POSTGRES_DB: mydb
    healthcheck:
      test: [ "CMD-SHELL", "pg_isready -U user -d mydb" ]
      interval: 30s
      timeout: 10s
      retries: 5
```

```yaml
services:
  redis:
    image: redis:6
    healthcheck:
      test: ["CMD-SHELL", "redis-cli ping"]
      interval: 30s
      timeout: 10s
      retries: 3
```

### Integrating Health Checks with the `depends_on` Directive

The `depends_on` directive in Docker Compose specifies service dependencies, ensuring that certain services start before others. However, by default, `depends_on` only waits for the dependent containers to start, not to become healthy or ready.

To make `depends_on` wait for a service to be healthy, Docker Compose supports condition-based dependencies. This integration ensures that a service only starts after its dependencies are reported as healthy.

```yaml
services:
  db:
    image: postgres:latest
    environment:
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
      POSTGRES_DB: mydb
    healthcheck:
      test: [ "CMD-SHELL", "pg_isready -U user -d mydb" ]
      interval: 10s
      timeout: 5s
      retries: 5
      start_period: 5s

  webapp:
    image: your-webapp-image
    ports:
      - "8080:8080"
    depends_on:
      db:
        condition: service_healthy
    healthcheck:
      test: ["CMD-SHELL", "curl -f http://localhost:8080/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

## Resources




