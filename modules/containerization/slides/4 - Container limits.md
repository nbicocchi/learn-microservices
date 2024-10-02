# Containers limits

### First Docker commands
Let’s try to start a container by launching an Ubuntu server using Docker’s run command: 

```
$ docker run -it --rm ubuntu
```

With the preceding command, we ask Docker to create a container that runs Ubuntu, based on the latest version that’s available. The -it option is used so that we can interact with the container using Terminal, and the --rm option tells Docker to remove the container once we exit the Terminal session.

The first time we use a Docker image that we haven’t built ourselves, Docker will download it from a Docker registry, which is Docker Hub by default (https://hub.docker.com). This will take some time, but for subsequent usage of that Docker image, the container will start in just a few seconds!

Once the Docker image has been downloaded and the container has been started up, the Ubuntu server should respond with a prompt such as the following:

```
$ docker run -it --rm ubuntu
root@a48641193673:/#
```

We can try out the container by, for example, asking what version of Ubuntu it runs:

```
# cat /etc/os-release | grep 'VERSION='
VERSION="22.04.3 LTS (Jammy Jellyfish)"
```

### Running Java in Docker
Java has not historically been very good at respecting limits set for Docker containers when it comes to the use of memory and CPU.

Instead of allocating memory inside the JVM in relation to the memory available in the container, Java allocated memory as if it had access to all the memory in the Docker host. When trying to allocate more memory than allowed, the Java container was killed by the host with an “out of memory” error message. In the same way, Java allocated CPU-related resources such as thread pools in relation to the total number of available CPU cores in the Docker host, instead of the number of CPU cores that were made available for the container JVM was running in.

In Java SE 9, initial support for container-based CPU and memory constraints was provided, much improved in Java SE 10.

Let’s look at how Java SE 21 responds to limits we set on a container it runs in!

```
# opens jshell inside the JVM containers
$ docker run -i --rm eclipse-temurin:21
```

### Limiting CPUs

This command will send the string Runtime.getRuntime().availableProcessors() to the Docker container, which will process the string using jshell:

```
echo 'Runtime.getRuntime().availableProcessors()' | docker run --rm -i eclipse-temurin:21
Apr 03, 2024 2:51:06 PM java.util.prefs.FileSystemPreferences$1 run
INFO: Created user preferences directory.
|  Welcome to JShell -- Version 21.0.2
|  For an introduction type: /help intro

jshell> Runtime.getRuntime().availableProcessors()
Runtime.getRuntime().availableProcessors()$1 ==> 8
```

Let’s move on and restrict the Docker container to only be allowed to use three CPU cores using the --cpus option, then ask the JVM about how many available processors it sees:

```
echo 'Runtime.getRuntime().availableProcessors()' | docker run --rm --cpus=2 -i eclipse-temurin:21
Apr 03, 2024 2:52:36 PM java.util.prefs.FileSystemPreferences$1 run
INFO: Created user preferences directory.
|  Welcome to JShell -- Version 21.0.2
|  For an introduction type: /help intro

jshell> Runtime.getRuntime().availableProcessors()$1 ==> 2
```

The JVM now responds with 2; that is, Java SE 21 honors the settings in the container and will, therefore, be able to configure CPU-related resources such as thread pools correctly!

### Limiting memory

In terms of the amount of available memory, let’s ask the JVM for the maximum size that it thinks it can allocate for the heap:

```
$ docker run -it --rm eclipse-temurin:21 java -XX:+PrintFlagsFinal | grep "size_t MaxHeapSize"
size_t MaxHeapSize = 2061500416                                {product} {ergonomic}
```

With no JVM memory constraints (that is, not using the JVM parameter -Xmx), Java will allocate one-quarter of the memory available to the container for its heap (in this case about 2GB).

If we constrain the Docker container to only use up to 1 GB of memory using the Docker option -m=1024M, we expect to see a lower max memory allocation.

```
$ docker run -it --rm -m=1024M eclipse-temurin:21 java -XX:+PrintFlagsFinal | grep "size_t MaxHeapSize"
size_t MaxHeapSize = 268435456                                 {product} {ergonomic}
```

Approximately 256MB is one-quarter of 1 GB, so again, this is as expected.

We can also set the max heap size on the JVM ourselves. For example, if we want to allow the JVM to use 600 MB of the total 1 GB we have for its heap, we can specify that using the JVM option -Xmx600m like so:

```
$ docker run -it --rm -m=1024M eclipse-temurin:21 java -Xmx600m -XX:+PrintFlagsFinal -version | grep "size_t MaxHeapSize"
size_t MaxHeapSize = 629145600                                 {product} {ergonomic}
```

Let’s conclude with an “out of memory” test to ensure that this really works! We’ll allocate 10MB of memory using jshell in a JVM that runs in a container that has been given 256MB of memory; that is, it has a max heap size of 64MB.

```
echo "new byte[10_000_000]" | docker run -i --rm -m=256M eclipse-temurin:21
Jan 15, 2024 4:03:08 PM java.util.prefs.FileSystemPreferences$1 run
INFO: Created user preferences directory.
|  Welcome to JShell -- Version 17.0.8.1
|  For an introduction type: /help intro

...

jshell> % 
```

This works fine. Let's move from 10MB to 100MB!

```
echo "new byte[100_000_000]" | docker run -i --rm -m=256M eclipse-temurin:21
Jan 15, 2024 4:03:50 PM java.util.prefs.FileSystemPreferences$1 run
INFO: Created user preferences directory.
|  Welcome to JShell -- Version 17.0.8.1
|  For an introduction type: /help intro

jshell> new byte[100_000_000]|  Exception java.lang.OutOfMemoryError: Java heap space
|        at (#1:1)

jshell> %  
```

The JVM sees that it can’t perform the action since it honors the container settings of max memory and responds immediately with Exception java.lang.OutOfMemoryError: Java heap space. Great!

## Resources