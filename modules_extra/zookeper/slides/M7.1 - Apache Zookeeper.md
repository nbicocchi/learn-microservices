# Apache Zookeeper

Zookeeper is a centralized service for maintaining configuration information, naming, providing distributed synchronization, and providing group services. These types of services are used by distributed applications. However, the implementation is complicated because of possible race conditions and other bugs. Because of the difficulty of implementing these types of services, applications initially ignore them, making them fragile when changes occur and difficult to manage. Even when properly implemented, the various implementations of these services result in management complexity when applications are deployed.

Zookeeper aims to collect these different services into a simple interface for a centralized coordination service. The service itself is distributed and highly reliable. Configuration information, naming, and others, will be implemented by the service so that applications do not have to implement them themselves.

## Overview

Zookeeper, much like a file system, allows distributed processes to coordinate with each other through a shared hierarchical namespace of data registers (called znodes). Unlike normal file systems, however, Zookeeper provides its clients with high-speed, low-latency, high-availability, orderly access to znodes. These features allow Zookeeper to be used in large distributed systems. In addition, the reliability of Zookeeper, prevents it from becoming a single point of failure.

The namespace provided by Zookeeper is very similar to that of a standard file system. A name is a sequence of path elements separated by a slash ("/"). Each znode in the Zookeeper namespace is identified by a path. Each znode has a parent whose path is a prefix of the znode with one less element; the exception to this rule is root ("/") which has no parent. Also, just like standard file systems, a znode cannot be deleted if it has children.

The main difference between Zookeeper and standard file systems is that znodes are limited in the amount of data they can have. Zookeeper is designed to store coordination data: information about status, configuration, location, etc. This type of meta-information is usually measured in kilobytes. To prevent Zookeeper from being used as a large data store, it has a built-in integrity check of 1M.

![](../../../slides/images/zookeeper-overview.png)

The service itself is replicated to a set of component machines. These machines maintain an in-memory image of the data tree, along with transaction logs and snapshots in a persistent archive. Because the data is stored in memory, Zookeeper is able to achieve very high throughput and low latency values. The cons, however, of an in-memory database is that the size of the database that Zookeeper can handle is limited. This limitation is another reason for keeping the amount of data stored in the znodes small.

The servers that make up the Zookeeper service must all know each other. As long as most of the servers are available, the Zookeeper service will be available. Clients must also know the list of servers. Clients create a handle to the Zookeeper service using this list of servers.

Clients connect to only a single Zookeeper server. The client maintains a TCP connection through which it sends requests, receives responses, receives observation events, and sends heartbeats. If the TCP connection to the server is broken, the client connects to another server. When a client first connects to the Zookeeper service, the first Zookeeper server sets up a session for the client. If the client needs to connect to another server, the session is reestablished with the new server.

The order is very important for Zookeeper. Zookeeper marks each update with a number that reflects this order. This number is called a zxid (Zookeeper Transaction Id). Each update will have a unique zxid. Reads are ordered with respect to updates. Responses to reads are marked with the last zxid processed by the server handling the read.

## Design goals

**Zookeeper is simple**. Zookeeper allows distributed processes to coordinate with each other through a shared hierarchical namespace, organized similarly to a standard file system. The namespace consists of data records (called znodes), which are similar to files and directories. Unlike a typical file system, which is designed for storage, Zookeeper's data is stored in memory. This ensures that Zookeeper can achieve high throughput numbers and low latency.

**Zookeeper is replicated**. Like the distributed processes it coordinates, Zookeeper is intended to be replicated on a set of hosts called an ensemble.

![](../../../slides/images/zookeeper-design-goals.jpg)

The servers that make up the Zookeeper service must know about each other: they maintain an in-memory image of the state, along with transaction logs and snapshots in a persistent repository. Because of this, as long as most of the servers are available, the Zookeeper service will be available.

**Zookeeper is ordered**. Zookeeper marks each update with a number that reflects the order of all Zookeeper transactions. Subsequent transactions can use the order to implement higher-level abstractions, such as synchronization primitives.

**Zookeeper is fast**. It is especially fast in "read-dominant" workloads. Zookeeper's applications run on thousands of machines, and its performance is best when reads are more frequent than writes.

## Data model

In Zookeeper, data are stored in a hierarchical namespace, similar to that of a file system: a name is a sequence of path elements separated by a slash (/); in addition, each node in the Zookeeper namespace is identified by a path.

![](../../../slides/images/zookeeper-data-model.jpg)

Each namespace node is called a Znode and can store data and have children. Znodes are similar to files and directories in a file system. Zookeeper provides a simple API for creating, reading, writing, and deleting Znodes.

## Types of Znodes

A Znode can be:
- **Persistence**: Alive until they’re explicitly deleted.
- **Ephemeral**: Active until the client connection is alive.
- **Sequential**: Either persistent or ephemeral. When created, is automatically assigned a unique sequence number as part of its name. This feature is particularly useful for implementing synchronization and coordination patterns in distributed systems.

## Updates

Zookeeper supports the concept of watches. Clients can set a watch on a znode. A watch will be triggered and removed when the znode changes. When a watch is triggered, the client receives a packet saying that the znode has changed. If the connection between the client and one of the Zookeeper servers is broken, the client will receive a local notification.

## API

Zookeeper providing a very simple programming interface. As a result, it supports these operations:
- **create**: creates a node at a location in the tree
- **delete**: deletes a node
- **exists**: tests if a node exists at a location 
- **get data**: reads the data from a node 
- **set data**: writes data to a node 
- **get children**: retrieves a list of children of a node 
- **sync**: waits for data to be propagated

## Implementation and components

Except the request processor, each server that makes up the Zookeeper service replicates its own copy of each of the components.

![](../../../slides/images/zookeeper-ensemble.jpg)

The replicated database is an in-memory database that contains the entire data tree. Updates are recorded to disk to ensure retrievability, and writes are serialized to disk before being applied to the in-memory database.

Each Zookeeper server serves clients. First, clients connect to a server to send requests, then read requests are served by the local replica of each server database, instead, write requests, are processed by an agreement protocol.

Under the agreement protocol, all client write requests are forwarded to a single server (called leader). Other Zookeeper servers (called followers), receive message proposals from the leader and agree on message delivery.

The messaging layer takes care of replacing the leaders in case of failure and synchronizing the followers with the leaders.

ZooKeeper uses an atomic messaging protocol. This ensures that, in Zookeeper, local replicas never diverge: when the leader receives a write request, it calculates what the state of the system is when write is to be applied and turns it into a transaction that captures this new state.

**Note**: This link explains in detail the synchronization implemented by the atomic broadcast: https://zookeeper.apache.org/doc/r3.1.2/zookeeperInternals.html#sc_atomicBroadcast

To conclude, components in Zookeeper are:
- **Leader and Follower**
- **Request Processor**: active in the leader node, it is responsible for processing write requests. After processing, it sends the changes to the follower nodes. 
- **Atomic Broadcast**: active in both the leader and follower nodes. It is responsible for sending the changes to the other nodes. 
- **In-memory databases (replicated databases)**: It is responsible for storing data in the zookeeper. Each node contains its own databases. Data is also written to the file system to ensure recoverability in case of problems with the cluster. 
- **Client**: one of the nodes in the distributed application cluster. It accesses information from the server. Each client sends a message to the server to let the server know that the client is alive. 
- **Server**: provides all services to the client. Provides an acknowledgement to the client. 
- **Ensemble**: group of Zookeeper servers. To ensure high availability and fault tolerance, the minimum number of nodes required to form an ensemble is 3. But, for a simple application, we can use only one node. Finally, ZooKeeper uses a quorum-based consensus algorithm, which means that for any write or edit operation, the majority of servers in the cluster must agree.

# Resources

- https://zookeeper.apache.org/
- https://cwiki.apache.org/confluence/display/ZOOKEEPER/Index
- https://spring.io/projects/spring-cloud-zookeeper
