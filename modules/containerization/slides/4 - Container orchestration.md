# Docker Compose Overview
Docker Compose is a powerful tool that allows us to define and manage multi-container Docker applications. It is particularly useful when working with microservice ecosystems, as it enables the easy launch and coordination of multiple containers simultaneously. With Compose, we can configure networking, define infrastructure as code, and also address scalability requirements.

## `docker-compose.yml`
The Compose file is the heart of Docker Compose, it consists of a YAML file (`docker-compose.yml`) that allows to define an application ecosystem including services, networking, disk space and more.

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

### Basic example of `docker-compose.yml`
Now we will deploy a simple Flask application mapped to the port 5000 (code/compose-simple).

`Project structure`
```
compose-simple/
│
├── app/
│   ├── app.py
│   └── requirements.txt
└── docker-compose.yml
```

```Dockerfile
FROM python:3.9-slim
WORKDIR /app
COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt
COPY . .
CMD ["python", "app.py"]
```

```yaml
services:
  flask:
    build: app
    ports:
      - "5000:5000"
```

Now with everything set up, we will start the ecosystem with the `docker-compose up` command in the context of the project directory.

```bash
docker-compose up
```

To test everything we can `curl` or use a browser to `http://localhost:5000` to get the "Hello from Flask in Docker!" message. 


## Lifecycle di Docker Compose
Now that we know how to set up a simple ecosystem with compose, we will show how to really manage it with CLI commands. Docker Compose allows us to manage the ecosystem's lifecycle with `docker-compose <command>`:
  - **`up`**: Start all services defined in the`docker-compose.yml` file.
  - **`down`**: Stops and removes all containers, networks, volumes defined in the compose file.
  - **`build`**: Builds the Docker images specified in services.
  - **`logs`**: Views the logs of all containers in execution.

In the following sections we will introduce and explain various features of Docker compose, these features will allow us to set up and customize a multi-container ecosystem adapt to our necessities and use cases.

## Replicas
A **Replica** in Docker refers to the ability to instantiate more replicas of the same container, this allows us to scale it horizontally.
The `deploy` field allows us to manage replicas and is composed of:
 - `mode`: This indicates how to run the service **global** means one container per host or **replicated** to run on the same host machine.
   - `replicas`: the number of replicas.

> [!NOTE]
> If not specified, `mode` is defaulted to `replicated`


We will create an [updated version](https://github.com/NakajimaAkemi/Microservices-containerization/tree/master/workdir/replica-compose) of our first example by adding  to the compose file and defining the number of replicas.

```yaml
services:
  flask:
    build: app
    ports:
      - "5000"
    deploy:
      mode: replicated
      replicas: 3
```
> [!NOTE]
> Since the service we're replicating is mapping ports, we will specify only port number and not the range, otherwise it will show an error.

As standard procedure we will start up the ecosystem and see the running containers.

```bash
docker-compose up --detach
```

```bash
$ docker ps

CONTAINER ID   IMAGE        COMMAND         ...    PORTS
abc123         flask_app    "python app.py" ...    0.0.0.0:32768->5000/tcp
def456         flask_app    "python app.py" ...    0.0.0.0:32769->5000/tcp
ghi789         flask_app    "python app.py" ...    0.0.0.0:32770->5000/tcp
```

We can verify their reachability with a browser or the `curl` command (port numbers might vary):
 - http://localhost:32768
 - http://localhost:32769
 - http://localhost:32770


## Resource limitation
In Docker Compose we can limit CPU and memory for containers. To apply the resource limitation to a specific `service` we need to add the `resources` and `limits` attribute under `deploy`. 
Then, we can define the resource limitations with:
   - `memory`: specifies how much memory the container can allocate
   - `cpus`: fractions of CPU cores the container can use

```yaml
services:
  app:
    image: myapp
    deploy:
      resources:
        limits:
          memory: 512M    # Set the maximum memory limit (hard limit)
          cpus: '0.5'     # Restrict CPU usage to 50% of one CPU core
```

## Volumes and Bind Mounts
In this section we introduce `Volumes` and `Bind Mounts` as fundamental items for Docker containers that allow us to maintain persistent data surviving beyond the container lifecycle, facilitating sharing data between containers, backup and restore.
- `Volumes`: are directly managed by Docker and can survive beyond the container's lifecycle, usefully if we want persistence of our data.

- `Anonymous volumes`: are temporary volumes created by Docker when a container starts. They are typically used for temporary data, they are ephemeral and removed when the container is removed.

- `Bind mounts`: allow you to mount a directory or file from the host machine into the container (bind mounts depend on the host's absolute path).

| Feature              | Anonymous Volume                               | Named Volume                                      | Bind Mount                                      |
|----------------------|------------------------------------------------|---------------------------------------------------|-------------------------------------------------|
| **Definition**       | Automatically created by Docker without a name | Defined and named manually by the user            | Links a host directory/file to a container path |
| **Persistence**      | Temporary, removed with the container          | Persistent, exists independently of the container | Depends on the host filesystem                  |
| **Access from Host** | Not directly accessible                        | Accessible via Docker commands                    | Direct access from the host                     |
| **Typical Usage**    | Temporary or transient data                    | Persistent data (e.g., databases)                 | Sync files during development                   |
| **Security**         | Secure, Docker-managed                         | Secure, Docker-managed                            | Full host access                                |

_Is also important to say that if a service doesn't specify a volumes section, it means that the service won't have any volumes mounted. Essentially, **no data will be persisted outside the container** for that service._

In the following example (code/compose-volumes) we have a simple Flask echo server configured to save logs in '/data/log.txt'.
This file can be written inside the container or externalized with either volumes or bind mounts.

```python
from flask import Flask, request, jsonify
import datetime
import os

app = Flask(__name__)

LOG_FILE_PATH = '/data/log.txt'

@app.route('/echo', methods=['POST'])
def echo():
    data = request.json 
    with open(LOG_FILE_PATH, 'a') as f:
        f.write(f"I echoed the message  {data} at: {datetime.datetime.now()}</br>\n")
    return jsonify({"echoed_data": data})


@app.route('/logs')
def read_logs():
    if os.path.exists(LOG_FILE_PATH):
        with open(LOG_FILE_PATH, 'r') as f:
            log_content = f.read()
        return log_content
    else:
        return "Log file not found!", 404

if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True, port=5000)
```

We can send a message to the echo server with:

```bash
curl -X POST http://localhost:5000/echo -H "Content-Type: application/json" -d '{"message": "Hello, Echo Server!"}'
```

We can query the logs with:

```bash
curl -X GET http://localhost:5000/logs
```

### Volumes

```yaml
services:
  web:
    build: app  # Builds the service from the Dockerfile located in the 'app' directory
    ports:
      - "5000:5000"  # Maps port 5000 of the host to port 5000 of the container
    volumes:
      - logs:/data  # Attaches the 'logs' volume to the '/data' directory inside the container

volumes:
  logs:  # Defines a named volume called 'logs' which can persist data across container restarts
```

- **services**: Defines the services (containers) to be created. Here, only one service named `web` is defined.
- **build**: Instructs Docker to build the image using a Dockerfile located in the `app` directory.
- **ports**: Maps a port on the host (left side) to a port on the container (right side). Here, both are set to `5000`.
- **volumes**: Specifies volumes to be mounted inside the container. The `logs` volume is mounted at `/data` within the container, which could be used for persistent storage.
- **volumes** section: Defines the `logs` volume, which persists data independent of the container lifecycle.

```bash
$ docker compose -f docker-compose.volume.yaml up
```

### Anonymous Volumes

```yaml
services:
  web:
    build: app  # Builds the Docker image from the Dockerfile in the 'app' directory
    ports:
      - "5000:5000"  # Maps port 5000 on the host to port 5000 in the container
    volumes:
      - /data  # Creates an anonymous volume and mounts it to the '/data' directory inside the container
```

- **Anonymous Volume**: The path `/data` refers to an anonymous volume. Docker will automatically create a volume without a specific name, and it will be mounted to the `/data` directory inside the container. This volume is used for storing data but won't have a persistent identity unless managed externally (i.e., you won't be able to reuse or easily reference this volume by name later).
- **build**: Builds the Docker image from the Dockerfile in the `app` directory.
- **ports**: Maps port `5000` on the host to port `5000` inside the container, allowing access to the service running inside the container on that port.

### Bind mounts

```yaml
services:
  flask:
    build: app  # Builds the Docker image using the Dockerfile in the 'app' directory
    ports:
      - "5000:5000"  # Maps port 5000 on the host to port 5000 in the container
    volumes:
      - /tmp/data:/data  # Bind mounts the '/tmp/data' directory from the host to the '/data' directory inside the container
```

- **Bind Mount**: The path `/tmp/data:/data` specifies a bind mount. This means that the directory `/tmp/data` on the host machine is directly mounted into the container at `/data`. Any changes made in the container at `/data` will be reflected on the host's `/tmp/data` directory and vice versa.
- **build**: Instructs Docker to build the image using the Dockerfile in the `app` directory.
- **ports**: Maps port `5000` on the host to port `5000` inside the container, allowing external access to the service.

## Networks
One aspect that makes the Docker engine a powerful tool is the possibility of creating and managing the services' connectivity. When using Docker Compose, networks are automatically created for your services, but you can also define custom networks to:
- Isolate groups of services. 
- Control which services can communicate with each other. 
- Configure advanced network options like driver types and external networks.

Docker supports different types of networks:
- **Bridge**: The default network type for Docker containers. It isolates containers from the host network but allows communication between containers on the same bridge network.
- **Host**: Uses the host's networking stack directly. Containers share the host's network interfaces, which can lead to performance improvements but less isolation.
- **Overlay**: Allows containers to communicate across different Docker hosts. It's commonly used in swarm mode to manage clusters of containers.
- **Macvlan**: Assigns a MAC address to a container, allowing it to appear as a physical device on the network. Useful for applications that require direct access to the physical network.

### Interaction with `iptables`

Docker manages networking using `iptables`, a Linux utility for configuring network packet filtering rules. Here's how Docker Compose networks interact with `iptables`:

#### Automatic `iptables` Rules
When you create a network (either by running `docker-compose up` or manually), Docker automatically adds `iptables` rules to allow traffic between containers in the same network.

Each bridge network is assigned a subnet, and `iptables` rules are configured to allow communication between all containers on that subnet.

#### Isolation and Security
Docker uses `iptables` to enforce isolation between networks. Containers on different networks cannot communicate with each other unless explicitly allowed through additional rules.

You can customize `iptables` rules if you need finer control over traffic flow. For example, you might want to restrict access to a database container from the outside world while allowing specific application containers to connect.

#### Inspecting `iptables` Rules
You can inspect the `iptables` rules applied by Docker using the following command:
```bash
sudo iptables -L -n
```
This command lists all the rules, including those created by Docker. Look for chains like `DOCKER` that contain rules specific to Docker containers.

#### Network Management Commands
To list the networks created by Docker, you can run:
```bash
docker network ls
```

To inspect a specific network and see its details, including the associated containers, use:
```bash
docker network inspect <network_name>
```



### Networks in Docker Compose
Networks can be explicitly defined in `docker-compose.yml`. By default, Compose creates a single network that connects all the services. To control network behavior, you can define multiple networks and specify which services are connected to each.

When defining networks in `docker-compose.yml`, inside the top-level attribute `networks` the following attributes can be specified:

- **`driver`** : Specifies the network driver (default is `bridge` for single-host networking). 
- **`driver_opts`**: Allows you to specify custom options for the chosen driver.
- **`attachable`**: Allows containers to be manually attached to this network.
- **`ipam`**: IP Address Management: lets you define custom IP address ranges, subnets, and gateways.
- **`external`**: Indicates if the network is external (i.e., already created outside Compose).

### Network example
In this example (code/compose-network) we define the `backend` and `postgres` services connected to a custom network called `my_custom_network`, using a `bridge` driver, which is the default for single-host setups.

```yaml
services:
  postgres:
    image: postgres:latest
    container_name: postgres
    restart: always
    environment:
      POSTGRES_USER: user
      POSTGRES_PASSWORD: secret
      POSTGRES_DB: jdbc_schema
    volumes:
      - pg-data:/var/lib/postgresql/data
    networks:
      - my_custom_network
    healthcheck:
      test: [ "CMD-SHELL", "pg_isready -U user -d jdbc_schema" ]
      interval: 30s
      timeout: 10s
      retries: 5

  backend:
    build:
      context: backend
    depends_on:
      - postgres
    ports:
      - "5000:5000"
    networks:
      - my_custom_network
    environment:
      - DATABASE_URL=postgresql://user:secret@postgres:5432/jdbc_schema

volumes:
  pg-data:

networks:
  my_custom_network:
    driver: bridge
```

### Isolated Networks example
A good way to use networks is for isolating a portion of the microservice ecosystem that does not need to be exposed to external networks. 
In this example (code/compose-isolation), we define two networks `front_net` and `back_net`, this will allow us to hide the backend side of the network. 

![](images/ecosystem.avif)

In this example:
- The `frontend` and `backend` services share the `front_net`, allowing them to communicate.
- The `backend` and `database` services share the `back_net`, isolating database traffic from the frontend.
- `nginx` is used as a reverse proxy for API requests forwarding them to Flask

```yaml
services:
  frontend:
    image: nginx
    volumes:
      - ./frontend/nginx.conf:/etc/nginx/nginx.conf  # Custom NGINX config
    networks:
      - front_net
    ports:
      - "8080:8080"  # Expose the frontend on port 80

  backend:
    build:
      context: backend  # Build backend from the local directory
    networks:
      - front_net
      - back_net
    environment:
      - DATABASE_URL=postgresql://user:secret@postgres:5432/jdbc_schema

  postgres:
    image: postgres:latest
    container_name: postgres
    restart: always
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
      interval: 30s
      timeout: 10s
      retries: 5

networks:
  front_net:
    driver: bridge
  back_net:
    driver: bridge

volumes:
  pg-data:
```

## Resources




