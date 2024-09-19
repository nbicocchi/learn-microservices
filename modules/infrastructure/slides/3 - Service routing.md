# Service Routing

In microservice architectures, usually comes a point where we’ll need to ensure that critical *cross-cutting concerns* such as security, logging, and tracking users across multiple service calls occur. To implement this functionality, **we want these functionalities to be consistently enforced across all services** without the need for each team to build their own solution. 

## Implementing cross-cutting concerns with a shared library
While it’s possible to use a common library for embedding these capabilities into service, doing so has several (negative) implications:
* **Consistency:** It’s challenging to implement these capabilities in each service consistently. 
* **Bug Proness:** Pushing the responsibilities to implement cross-cutting concerns like security and logging down to the individual teams greatly increases the odds that someone will not implement them properly or will forget to do them. 
* **Flexibility:** It’s possible to create a hard dependency across all our services. The more capabilities we build into a common framework shared across all our services, the more difficult it is to change or add behavior in our common code without having to recompile and redeploy all our services. Suddenly an upgrade of core capabilities built into a shared library becomes a long migration process.

## Implementing cross-cutting concerns with a gateway service
To solve these issues, we need to abstract these cross-cutting concerns into a service that can sit independently and act as a filter and router for all the microservice calls in our architecture. We call this service a *gateway*. Clients no longer directly call a microservice. Instead, all calls are routed through the service gateway, which acts as a single *Policy Enforcement Point (PEP)*, and are then routed to a final destination.

The use of a centralized *PEP* means that cross-cutting service concerns can be carried out in a single place without the individual development teams having to implement those concerns. Examples of cross-cutting concerns that can be implemented in a service gateway:
* **Static routing** A service gateway places all service calls behind a single URL and API route. This simplifies development as we only have to know about one service endpoint for all of our services.
* **Dynamic routing** A service gateway can inspect incoming service requests and, based on the data from the incoming request, perform intelligent routing for the service caller. For instance, customers participating in a beta program might have all calls to a service routed to a specific cluster of services that are running a different version of code.
* **Authentication and authorization** Because all service calls route through a service gateway, the service gateway is a natural place to check whether the callers of a service have authenticated themselves.
* **Metric collection and logging** A service gateway can be used to collect metrics and log information as a service call passes through it. You can also use the service gateway to confirm that critical pieces of information are in place for user requests, thereby ensuring that logging is uniform. 

## Resources
- Spring Microservices in Action (Chapter 8)
