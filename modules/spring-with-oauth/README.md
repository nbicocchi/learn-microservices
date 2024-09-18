# oauth2-spring-boot
An implementation of OAuth2 authorization using Spring Boot. <br>
Written and developed for the [Distributed Edge Programming exam](https://github.com/nbicocchi/learn-microservices/) @ Unimore.

# What is OAuth2 (and why should I use it)?
OAuth2 is the microservice industry's de-facto standard for authorization that enables a third-party application to obtain limited access to an HTTP service, either on behalf of a resource owner by orchestrating an approval interaction between the resource owner and the HTTP service, or by allowing the third-party application to obtain access on its own behalf.<br>
What this means is that with OAuth2 we can authorize access to a set of microservices through a standard layer that acts completely separately from our application, keeping responsibilities separated in our system.

The challenge of authorizing requests in a microservice-driven system is quite a difficult task: should every microservice have its own database where it stores user-related information? That's a terrible prospect to even imagine. Should every microservice refer to a central location to access authentication and authorization data? Well, yes, but how do we do it in a resilient and highly available fashion? Also, what happens when microservices need to make requests in a programmatic way (i.e. not driven by user input)? How do we authenticate such requests?<br>
OAuth2 addresses these (and more) problems and provides a framework that can help us navigate through the impervious world of microservice authorization.

# Table of contents 
This project is composed of five chapters, plus an appendix:<br>
* [Chapter I: introduction to OAuth2 with Keycloak](chapters/Chapter%20I.md)
* [Chapter II: the system and its components — how everything connects](chapters/Chapter%20II.md)
* [Chapter III: base project](chapters/Chapter%20III.md)
* [Chapter IV: implementing authentication](chapters/Chapter%20IV.md)
* [Chapter V: for the fearless — eliminating cross cutting concerns from the authorization layer with NGINX](chapters/Chapter%20V.md)
* [Appendix: refreshing access tokens](chapters/Appendix.md) 

Note: you can import the Postman collections (files named `oauth2-spring-boot-*.json` in this repo) to follow along more easily!
