# Labs

## Lab 1: Introduction to Distributed Data Management
**Objective:** Understand the principles of distributed data management and its challenges.

**Instructions:**
- Create a simple distributed application using Spring Boot with two microservices: an Order Service and a Payment Service.
- Each service should have its own database (e.g., H2 or MySQL) to demonstrate decentralized data management.
- Implement basic RESTful APIs for creating orders and processing payments.
- Discuss the challenges faced in data consistency and communication between the two services.

**Expected Outcomes:**
- Students will gain an understanding of distributed data management and the complexities involved.
- They will analyze the need for consistency and reliability in microservices architecture.

## Lab 2: Implementing the Saga Pattern
**Objective:** Learn how to implement the Saga pattern for managing distributed transactions.

**Instructions:**
- Extend the application from Lab 1 to implement the Saga pattern using a choreography approach.
- Define a series of compensating transactions for the Order and Payment services (e.g., if payment fails, cancel the order).
- Use a message broker (e.g., RabbitMQ) to handle events between services and implement the saga workflow.
- Test the implementation by simulating successful and failed payment scenarios.

**Expected Outcomes:**
- Students will understand how the Saga pattern manages distributed transactions and maintains data consistency.
- They will gain hands-on experience with event-driven communication between microservices.

## Lab 3: Using Conductor as a Workflow Manager
**Objective:** Implement a workflow manager using Conductor for orchestrating distributed workflows.

**Instructions:**
- Set up a Conductor server and configure it to manage workflows for the Order and Payment services.
- Define a workflow in Conductor that coordinates the order and payment processes, including success and failure paths.
- Implement the necessary API calls from the microservices to interact with Conductor.
- Test the entire workflow by creating an order and processing the payment, ensuring that Conductor manages the state and compensations correctly.

**Expected Outcomes:**
- Students will learn how to use Conductor as a workflow manager to orchestrate complex distributed processes.
- They will understand the benefits of using a centralized workflow manager for distributed data management.

# Questions
1. What are the key challenges in distributed data management, and how do they impact microservices?
2. Explain the differences between centralized and decentralized data management in microservices.
3. What is the Saga pattern, and why is it essential for managing distributed transactions?
4. Describe the two approaches to implementing the Saga pattern: choreography and orchestration.
5. How do compensating transactions work in the context of the Saga pattern?
6. Discuss the role of message brokers in implementing the Saga pattern for inter-service communication.
7. What is Conductor, and how does it facilitate the management of distributed workflows?
8. How do you define a workflow in Conductor, and what are its key components?
9. What are the advantages of using a workflow manager like Conductor over custom orchestration solutions?
10. Discuss best practices for implementing distributed data management and workflow orchestration in microservices.
