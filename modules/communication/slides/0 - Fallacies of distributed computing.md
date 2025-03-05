# Fallacies of distributed computing

### The Network is Reliable
This fallacy assumes that network connections are always stable and that there will be no disruptions. In reality, networks can fail due to various reasons like hardware issues, configuration errors, or transient faults.

```java
@RestController
public class NetworkReliabilityController {
    
    @PostMapping("/reliable")
    public ResponseEntity<String> reliableService(@RequestBody String data) {
        NetworkService remoteService = new NetworkService();
        String response = remoteService.process(data);
        return ResponseEntity.ok(response);
    }
}
```

```java
// version using dependency injection
@RestController
public class NetworkReliabilityController {
    NetworkService remoteService;
    
    @PostMapping("/reliable")
    public ResponseEntity<String> reliableService(@RequestBody String data) {
        String response = remoteService.process(data);
        return ResponseEntity.ok(response);
    }
}
```

How do you handle HttpTimeoutException? 
* Is the remote service still processing (legit timeout)?
* Is the data actually arrived at the remote service?
* Should we show the user an error (nah!), log it (mhhh), or retry (better!).

### Latency is Zero
This fallacy assumes that communication between services occurs instantaneously, neglecting the fact that **requests and responses take time**. The programming models used today (e.g., Dependency Injection) **hide performance differences**.

| Process                             | Duration | Normalized |
|-------------------------------------|----------|------------|
| 1 CPU cycle                         | 0.3ns    | 1s         |
| L1 cache access                     | 1ns      | 3s         |
| L2 cache access                     | 3ns      | 9s         |
| L3 cache access                     | 13ns     | 43s        |
| DRAM access (from CPU)              | 120ns    | 6min       |
| SSD I/O                             | 0.1ms    | 4days      |
| HDD I/O                             | 1-10ms   | 1-12months |
| Internet: San Francisco to New York | 40ms     | 4years     |
| Internet: San Francisco to London   | 80ms     | 8years     |
| Internet: San Francisco to Sydney   | 130ms    | 13years    |
| TCP retransmit                      | 1s       | 100years   |
| Container reboot                    | 4s       | 400years   |

### Bandwidth is Infinite
This fallacy assumes that the network can handle any amount of data being sent or received at any time without degradation in performance. In reality, bandwidth is limited, and **how much data you can move through the network has severe implications of the performance of the system**.

* 1 Gbps = 128Mbps
* Deduct TCP/IP overhead -> 64Mbps
* Deduct serialization overhead -> 32Mbps
* **As this limit is approached (e.g., additional machines are installed on the same network due to performance issues), slow things like TCP retransmits become more frequent, latency adds up ultimately impacting reliability (e.g. HttpTimeoutException)!**

### The Network is Secure
This fallacy assumes that all communications over the network are inherently secure. In reality, security must be explicitly designed into network communications to protect against threats like eavesdropping, tampering, and unauthorized access. Furthermore, as [Kevin Mitnick](https://en.wikipedia.org/wiki/Kevin_Mitnick) initially showed, **social aspects become more and more relevant as the organizations complexity increases**. 

For example, *everyone can find on Linkedin the DBA admins of a company, discover their habits on social media, and eventually "ask" them to put a recent organization backup on a pendrive and drop it from their car at a specific location.*

### Topology Doesn't Change
This fallacy assumes that the network structure remains constant over time. In reality, distributed systems often experience changes due to scaling, failure, or reconfiguration, which can impact service discovery and availability.

For example, a network topology change can introduce reliability issues when, *callback contracts* are used in a distributed system.
* Callbacks rely on a stable connection between the caller and the callee
* Disruptions in network communication can lead to delayed, lost, or duplicated callbacks
* **Prolonged waiting times in the caller can increase resource consumption, potentially leading to system failure**.

### There is One Administrator
This fallacy assumes a single authority manages and controls all aspects of the distributed system.
* Possible in small networks
  * Until admins get ~~run over by a truck~~ promoted

- **Will everything run smoothly if multiple admins roll out upgrades and patches simultaneously?**
- **If developers create configurations for every uncertainty, who will understand the actual impact of a specific set of settings?**
- **Investing time in clear, well-documented, and centralized configuration is essential.**


### Transport Cost is Zero
This fallacy assumes that transferring data across the network incurs no cost, neglecting the reality that **data transfer can lead to operational costs**, and resource usage, especially when dealing with large volumes of data.

* **Network hardware** has upfront and ongoing costs
* **Serialization** before crossing the network (and deserialization on the other side) takes time.
* In the cloud, It can be a big cost factor (e.g., 70K$ cloud bill at the end of the month)

### The Network is Homogeneous
This fallacy assumes that all components in the network are similar, ignoring the reality that different services may be implemented in various programming languages, run on different platforms, or operate under different configurations.

- Interoperability between .NET and Java used to be straightforward.
- Now, the landscape includes Go, Rust, Python, MongoDB, Cassandra, and loosely integrated systems over HTTP (e.g., REST), leading to challenges such as:
    - Incompatible JSON serializers/deserializers
    - Shifts from relational to NoSQL data models
- It will get worse before it gets better

## Resources
- [Fallacies of Distributed Systems](https://www.youtube.com/watch?v=8fRzZtJ_SLk&list=PL1DZqeVwRLnD3EjyciYAO82dT9Owiq8I5)

