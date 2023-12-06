# Test a REST API with curl

This tutorial gives a brief overview of testing a REST API using _curl._

**_curl_ is a command-line tool for transferring data, and it supports about 22 protocols, including HTTP.** This combination makes it a very good ad-hoc tool for testing our REST services.


## Command-line Options

**_curl_ supports over 200 command-line options**. We can have zero or more of them to accompany the URL in the command.

Before we use it for our purposes, let’s take a look at two that would make our lives easier.

### Verbose

When we’re testing, it’s a good idea to set the verbose mode on:

```plaintext
curl -v http://www.example.com/
```

As a result, the commands provide helpful information such as the resolved IP address, the port we’re trying to connect to, and the headers.

### Output

By default, _curl_ outputs the response body to standard output. Additionally, we can provide the output option to save to a file:

```plaintext
curl -o out.json http://www.example.com/index.html
```

This is especially helpful when the response size is large.

## HTTP Methods With _curl_

Every HTTP request contains a method. The most commonly used methods are GET, POST, PUT and DELETE.

### GET

This is the default method when making HTTP calls with _curl_. In fact, the examples previously shown were plain GET calls.

While running a local instance of a service at port 8082, we’d use something like this command to make a GET call:

```plaintext
curl -v http://localhost:8082/spring-rest/foos/9
```

Since we have the verbose mode on, we get a little more information along with the response body:

```plaintext
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8082 (#0)
> GET /spring-rest/foos/9 HTTP/1.1
> Host: localhost:8082
> User-Agent: curl/7.60.0
> Accept: */*
>
< HTTP/1.1 200
< X-Application-Context: application:8082
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Sun, 15 Jul 2018 11:55:26 GMT
<
{
  "id" : 9,
  "name" : "TuwJ"
}* Connection #0 to host localhost left intact
```

### POST

We use this method to send data to a receiving service, which means we use the data option.

The simplest way of doing this is to embed the data in the command:

```plaintext
curl -d 'id=9&name=baeldung' http://localhost:8082/spring-rest/foos/new
```

Alternatively, we can pass a file containing the request body to the data option like this:

```plaintext
curl -d @request.json -H "Content-Type: application/json"
  http://localhost:8082/spring-rest/foos/new
```

By using the above commands as they are, we may run into error messages like the following one:

```plaintext
{
  "timestamp" : "15-07-2018 05:57",
  "status" : 415,
  "error" : "Unsupported Media Type",
  "exception" : "org.springframework.web.HttpMediaTypeNotSupportedException",
  "message" : "Content type 'application/x-www-form-urlencoded;charset=UTF-8' not supported",
  "path" : "/spring-rest/foos/new"
}
```

This is because _curl_ adds the following default header to all POST requests:

```plaintext
Content-Type: application/x-www-form-urlencoded
```

This is also what the browsers use in a plain POST. In our usage, we’d usually want to customize the headers depending on our needs.

For instance, if our service expects JSON content-type, then we can use the -H option to modify our original POST request:

```plaintext
curl -d '{"id":9,"name":"baeldung"}' -H 'Content-Type: application/json'
  http://localhost:8082/spring-rest/foos/new
```

Windows command prompt has no support for single quotes like the Unix-like shells.

As a result, we’d need to replace the single quotes with double quotes, though we try to escape them wherever necessary:

```plaintext
curl -d "{\"id\":9,\"name\":\"baeldung\"}" -H "Content-Type: application/json"
  http://localhost:8082/spring-rest/foos/new
```

Besides, when we want to send a somewhat larger amount of data, it is usually a good idea to use a data file.

### PUT

This method is very similar to POST, but we use it when we want to send a new version of an existing resource. In order to do this, we use the -X option.

Without any mention of a request method type, _curl_ defaults to using GET; therefore, we explicitly mention the method type in the case of PUT:

```plaintext
curl -d @request.json -H 'Content-Type: application/json'
  -X PUT http://localhost:8082/spring-rest/foos/9
```

### DELETE

Again, we specify that we want to use DELETE by using the -X option:

```plaintext
curl -X DELETE http://localhost:8082/spring-rest/foos/9
```

## Custom Headers

We can replace the default headers or add headers of our own.

For instance, to change the Host header, we do this:

```plaintext
curl -H "Host: com.baeldung" http://example.com/
```

To switch off the User-Agent header, we put in an empty value:

```plaintext
curl -H "User-Agent:" http://example.com/
```

The most common scenario while testing is changing the Content-Type and Accept header. We just have to prefix each header with the -H option:

```plaintext
curl -d @request.json -H "Content-Type: application/json"
  -H "Accept: application/json" http://localhost:8082/spring-rest/foos/new
```

## Authentication

A [service that requires authentication](https://www.baeldung.com/spring-security-basic-authentication) would send back a 401 – Unauthorized HTTP response code, and an associated WWW-Authenticate header.

For basic authentication, we can **simply embed the username and password combination inside our request using the user option**:

```plaintext
curl --user baeldung:secretPassword http://example.com/
```

However, if we want to [use OAuth2 for authentication](https://www.baeldung.com/rest-api-spring-oauth2-angularjs), we first need to get the _access\_token_ from our authorization service.

The service response would contain the _access\_token:_

```plaintext
{
  "access_token": "b1094abc0-54a4-3eab-7213-877142c33fh3",
  "token_type": "bearer",
  "refresh_token": "253begef-868c-5d48-92e8-448c2ec4bd91",
  "expires_in": 31234
}
```

Now we can use the token in our Authorization header:

```plaintext
curl -H "Authorization: Bearer b1094abc0-54a4-3eab-7213-877142c33fh3" http://example.com/
```

## Resources
- https://www.baeldung.com/postman-testing-collections
- https://www.baeldung.com/rest-assured-tutorial