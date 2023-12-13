# Annex: Introduction to Web Development Concepts (text-only)

In this lesson, we’ll understand some **basic concepts of web development.**

First, we will learn some fundamental concepts about the web and then we’ll explore some of the technologies around web development.

**If you're already familiar with these, you can skip this lesson.** These concepts are not part of Spring, but they are a prerequisite to understanding web development with Spring, so I decided to include this in the Curriculum, even though it's not strictly in scope for the course.

## Client-Server Model

Web applications function on top of the client-server network model.

This model involves two types of network entities communicating over the HTTP protocol.

### Server

In broad terms, a server is a sample of software or hardware that serves a specific service to its clients.

In the context of web applications, **a web server is a software that stores and serves content to its clients.** The content can be of any type such as text, HTML, image, audio, video, etc.

Content can also be application data in multiple formats like JSON, XML, PDF, etc.

### Client

A client is a software that connects to a server to access a service. **For web applications, the most common client is a web browser** that requests content from a web server and processes the response.

### Basic Flow

At a basic level, **a browser requests a certain resource from the web server via HTTP.**

The web server can be located within the client’s Local Area Network or anywhere on the World Wide Web.

When the request reaches the web server, the web server accepts the request, figures out what resource the client is requesting, and responds back, also through HTTP.

Multiple clients may connect to a single centralized server to serve their requests.

Note that the server returns the content to the client only when the client requests it. **The communication is initiated by the client:**

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/xN8BVg7CSMS57e34gR6b)

## HTTP Protocol

As mentioned previously, **the web server and the browser communicate over HTTP,** which stands for Hypertext transfer protocol.

Traditionally, data was transferred between clients and servers in the form of Hypertext documents (or HTML), which is where the protocol's name comes from. However, in modern systems, HTTP is used to serve not only HTML pages but also application data in various formats like JSON or XML.

Now let’s see how the HTTP protocol is used by the client and the server to communicate.

### HTTP Request

An HTTP request is triggered by the client to request some content or perform some action.

A typical HTTP request looks like this:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/4pucP6gS5SHF4nKbRDng)

Let’s understand **the components of an HTTP request:**

-   **URL**

The URL is **the address of the web server.** It may contain subpaths like _/start-here_ which indicate the location of a particular resource that the client is requesting.

-   **HTTP Method**
    **The HTTP Method sometimes referred to as the HTTP verb, indicates the action that the client is expecting a server to perform.** For example, the most common HTTP methods are GET and POST.

A GET request indicates that the client is expecting the server to return some resource or content, while a POST request indicates that the client is submitting data to the web server. For example, information entered in an HTML form is submitted to the server via a POST request.

-   **Request Headers**

HTTP headers are **key-value pairs that communicate additional information to the server**, based on which the server can respond to the request in a certain way.

For example, the _Accept-Language_ header indicates to the server to serve the content in a specified language.

-   **Request Body**

The body of the request contains **the data that the client is sending to the server**. For example, the data entered by the user in a form is sent as a request body to the server.

Naturally, the request body is usually associated with POST requests.

### HTTP Response

Upon receiving the request, web servers process the request and return back an HTTP Response.

The response contains information that the client is capable of understanding and processing.

A typical HTTP response looks like this:

![](https://cdn.fs.teachablecdn.com/ADNupMnWyR7kCWRvm76Laz/https://www.filepicker.io/api/file/m9Lj0SjoT7VTIobyM8jg)

Let’s understand **the components of an HTTP response:**

-   **HTTP Status Code**
    **The HTTP status is a 3 digit code that broadly indicates whether the request was completed successfully or failed.**

Each status code has a meaning assigned to it. For example, a status code of 404 indicates that the requested resource was not found by the server, or a status code of 500 indicates that an internal error occurred at the server-side.

For a successful request, the server responds with the status code 200.

-   **Response Headers**

Similar to the request, the response also contains headers that communicate additional information to the client.

**The client understands these headers and based on them, it processes the response in a certain way.** For example, one of the most important headers is the Content-Type header, which indicates the format of the data being sent in the response. Using this header, the browser renders the content in a certain way.

-   **Response Body**

A successful HTTP GET request generally has a response body that **contains the content being served by the server.**

A typical response to a request can be HTML data that the browser renders on the UI.

## Web Technology

Now that we understand how the client and the server communicate, let’s review some of the most common technologies used to develop web applications.

In the context of development, the concerns of a web application are broadly categorized into two categories:

-   Backend
-   Frontend

### Backend

Backend is **software written in any server-side programming language** like JAVA, Python, PHP, etc., and is **capable of serving HTTP requests.**

In the JAVA ecosystem, there are multiple web servers like Tomcat, Jetty, WebLogic.

Developers write the server-side code inside a Servlet, which is essentially a class in Java. The Servlet can then be deployed inside the server’s Servlet container.

The Servlet container takes care of managing the HTTP requests and routes the requests to an appropriate Servlet.

### Frontend

A frontend is the user interface that is seen on the browser screen. It’s **the part of the website that the user interacts with.**

The frontend is typically built using a combination of languages such as HTML, JavaScript, and Cascading Style Sheets (CSS), and other frameworks built with these languages that make development easier. Let's review these briefly.

**HTML**

HTML is a markup language that is used to define a skeleton or structure of a web page. It's the standard language for creating web pages.

A developer defines the structure of the web page using a series of elements and tags in an HTML document. The browser is capable of interpreting the HTML syntax and renders the content on the browser screen.

**CSS**

CSS or Cascading Style Sheets is basically used to add style to a web page.

While an HTML element defines the semantics of the content, CSS beautifies the content. For example, simple things like font, color, alignment of the content are controlled by CSS.

**Javascript**

Javascript controls the logical component of the UI.

Javascript is a scripting language that lets us control anything dynamic happening on the web page. Defining logic like what happens when a user clicks a button, or when the user scrolls, etc. can all be controlled by Javascript.

## Resources
- [Client vs Server terminology](https://www.baeldung.com/cs/client-vs-server-terminology)
- [HTTP overview](https://developer.mozilla.org/en-US/docs/Web/HTTP/Overview)
- [Introduction to Tomcat](https://www.baeldung.com/tomcat)
- [Introduction to Java Servlets](https://www.baeldung.com/intro-to-servlets)
- [HTML overview](https://developer.mozilla.org/en-US/docs/Web/HTML)
- [CSS overview](https://developer.mozilla.org/en-US/docs/Web/CSS)
- [Javascript overview](https://developer.mozilla.org/en-US/docs/Web/JavaScript)
- [Spring Boot With Angular example](https://www.baeldung.com/spring-boot-angular-web)

