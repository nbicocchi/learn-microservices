<!-- TOC start (generated with https://github.com/derlin/bitdowntoc) -->

# Table of contents

- [Chapter I: introduction to OAuth2 with Keycloak](#chapter-i-introduction-to-oauth2-with-keycloak)
   * [The nuts and bolts of OAuth2](#the-nuts-and-bolts-of-oauth2)
      + [Authorization code flow](#authorization-code-flow)
      + [Service account flow](#service-account-flow)
      + [Access token usage](#access-token-usage)
      + [Refresh token usage](#refresh-token-usage)
   * [Keycloak](#keycloak)
      + [Starting up](#starting-up)
      + [Realms](#realms)
      + [Clients](#clients)
         - [Creating a confidential client in Keycloak](#creating-a-confidential-client-in-keycloak)

<!-- TOC end -->

<!-- TOC --><a name="chapter-i-introduction-to-oauth2-with-keycloak"></a>
# Chapter I: introduction to OAuth2 with Keycloak
We mentioned that OAuth2 is a protocol, a _standard_, not something we can run: OAuth2 is a specification, not an application.<br>
To leverage the OAuth2 standard we must use a OAuth2-compliant __identity management provider__ (IDP), which is fancy for "a system composed by one or more servers that all run an application that does the authorization and authentication instead of us".
<br>
One of the leading IDPs is [Keycloak](https://www.keycloak.org/), developed and maintained by RedHat, which also happens to be completely open source and self-hostable. Other than that, Keycloak has a [vibrant and large community](https://github.com/keycloak/keycloak/pulls), definitely a plus when it comes to open source self-hostable projects.

<!-- TOC --><a name="the-nuts-and-bolts-of-oauth2"></a>
## The nuts and bolts of OAuth2
How do we effectively gain access to resources protected by an OAuth2 authorization scheme? The answer is simple: through a __flow__, a standard set of steps that the IDP will go through while authenticating a client's identity.<br>
The main flows that we will use in our journey through this project are the __authorization code flow__ and the __service account flow__. <br>
The first is used when the application that is requesting access to a protected resource is a __web__ or __mobile__ application, or really any application that is __user-driven__, whereas the latter is used when we need to authenticate __requests made from other programs that are not user-driven__ (i.e. a microservice!).<br>
Both of these flows will output two, very important, pieces of information: the __`access token`__ and the __`refresh token`__, which are the foundations to OAuth2's authorization system.<br>

- The `access token` is used to __issue authenticated requests to our microservices__. It's our passport __into the system__ and it has a very short lifespan: this is a key characteristic of access tokens; tokens that grant __access__ to protected resources _should_ have a short TTL, because __tokens leak__, security protocols are __breached__, things always take the wrong turn with microservices. For these reasons, access tokens should live as short as possible, because, if stolen, they will grant access to the system on behalf of the user that got the token in the first place.
This token can be `Opaque`, a random, meaningless string, or a `JWT` that encodes information. We will use a `JWT`.
- The `refresh token` is used to __get new access tokens when they expire__. They are (very) long-lived with respect to __access tokens__ and, needless to say, they are equally important and must be stored carefully and safely.

<!-- TOC --><a name="authorization-code-flow"></a>
### Authorization code flow
Here's a picture that describe how the user gets into the system with the authorization code flow.<br>
We will use the "basic" version, without [PKCE](https://oauth.net/2/pkce/), because of simplicity and also because PKCE is mainly related to mobile apps (but it's an additional layer of security that every client can benefit of, web apps included).
![auth code flow](assets/auth_code_flow.png)

1. the user clicks on the login button (or follows a login link);
2. the backend issues an authorization code request, this starts a redirection process to the login form. The authorization code will make sure that the request for the access and refresh token is not forged.
3. the user gets redirected to the login form;
4. the user authenticates;
5. if the credentials are correct, the auth code is issued and sent to the backend;
6. once the auth code is received, the backend issues another request to retrieve the tokens. Note, again, that the auth code's presence is critical to mitigate security risks: by issuing an auth code, the client __never directly contacts the OAuth2 server__ and the authentication flow is completely delegated to the server. This enforces security and doesn't allow privileged access to the OAuth2 server from potentially vulnerable clients; imagine what would happen if the authorization code didn't pass through the server, but was instead passed to a vulnerable client: if the authorization code gets intercepted by an attacker, the entire authorization session would be stolen, and malicious third parties could authenticate on the vulnerable client's behalf. Instead, it's the backend that requests credentials with the authorization code and the OAuth2 server is configured to accept authentication requests from the server only, thus, hardening security;
7. the OAuth2 server validates the code and the credentials;
8. the tokens are issued and...
9. ...a session is issued between the backend and the frontend;
10. the frontend can now make authenticated requests to other microservices;

<!-- TOC --><a name="service-account-flow"></a>
### Service account flow
As already mentioned, the service account flow is used by non-user driven processes, i.e. applications that aggregate user data (CQRS microservices) or notification services that query other microservices for reports and similar things.
![auth code flow](assets/service_account_flow.png).

1. the microservice has some pre-generated credentials with which it will make a request for tokens;
2. the OAuth2 service validates the credentials...
3. ...and returns the tokens to the microservice;
4. authenticated requests can now be sent to other microservices.

Notice how the flow is much shorter and simpler: that's because one can make stronger assumptions about the microservice, which means that it can be presumed much less likely to be vulnerable with respect to malicious third parties.<br>
Nevertheless, credentials on the microservice must be safely handled and rotated periodically.

<!-- TOC --><a name="access-token-usage"></a>
### Access token usage
Once the access token is obtained, it can be used to make authenticated request towards the protected API (in the picture above we can represent the protected API with the "microservices" block).<br>
It is the microservice's responsibility, then, to  verify the token's validity.<br>
For simplicity's sake, we are not going to delve into cryptographical details on how JWTs are created: we just need to know that JWTs are cryptographically signed strings that encode information in JSON format. Don't worry too much about them, we will delve deeper into this topic later in [Chapter IV](Chapter%20IV.md).<br>
The signing happens on the OAuth2 server's side with a private key and each microservice that will need to validate tokens issued by this server will receive a copy of the server's public key: this way, the verification is done completely "offline", meaning that microservices won't need to interrogate the OAuth2 server every time it receives an authenticated request.

We will see in detail how this process works in the next chapters, for now that's all we need to know.

<!-- TOC --><a name="refresh-token-usage"></a>
### Refresh token usage
Once the access token expires, verifying the JWT will return an error. That's when the client should __refresh__ its access token.
<br>
The refresh mechanism is very simple: the OAuth2 server exposes a refresh token endpoint to which applications can request new access tokens.<br>
Note that here, too, the process happens entirely on the backend side (for the same reasons we talked about before) and it's the auth microservice's (the one represented in the first image of this section) responsibility to ensure the token is refreshed correctly.
<br>
Here's a quick breakdown of how the process works, again, we will see it in much more detail later.<br>
![refresh token flow](assets/refresh_token.png)

<!-- TOC --><a name="keycloak"></a>
## Keycloak
Now that we talked about the theory, let's put it into practice with Keycloak.
<br>
We will use Keycloak's Docker image because we will model our system through a Docker Compose file for simplicity and reproducibility.
<!-- TOC --><a name="starting-up"></a>
### Starting up
Let's fire up a basic Keycloak instance: to do so, use the following `compose.yml` file
```yaml
keycloak:
    build:
      context: .
      dockerfile: ./path/to/Dockerfile
      network: host
    entrypoint: ["/opt/keycloak/bin/kc.sh", "start-dev"]
    environment:
      - KC_HOSTNAME=localhost
      - KC_HOSTNAME_PORT=8080
      - KC_HOSTNAME_STRICT=false
      - KC_HOSTNAME_STRICT_HTTPS=false
      - KEYCLOAK_ADMIN=admin
      - KEYCLOAK_ADMIN_PASSWORD=admin
    ports:
      - 8080:8080
```
Keycloak requires a Dockerfile to start, because it needs to generate a private/public keypair for crypto operations:
```Dockerfile
FROM quay.io/keycloak/keycloak:latest as builder

# Enable health and metrics support
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

WORKDIR /opt/keycloak
# for demonstration purposes only, please make sure to use proper certificates in production instead
RUN keytool -genkeypair -storepass password -storetype PKCS12 -keyalg RSA -keysize 2048 -dname "CN=server" -alias server -ext "SAN:c=DNS:localhost,IP:127.0.0.1" -keystore conf/server.keystore
RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:latest
COPY --from=builder /opt/keycloak/ /opt/keycloak/
```
Everything is ready, we just need to start our container:
```bash
docker compose up 
````
Note: we are not going to delve into the details of a production-ready deployment with Keycloak because it's out of the scope of this project. This setup will be the same we are going use throughout the whole project and, of course, it's not production ready since it uses an in-memory database to store user information, which is not ideal for a highly available environment.

<!-- TOC --><a name="realms"></a>
### Realms
Now that we fired up our Keycloak instance, let's navigate to [http://localhost:8080](http://localhost:8080) and login with the default credentials ("admin", "admin"). The interface should look something like this:
![keycloak home](assets/keycloak_1.png)

Keycloak introduces the concept of __realms__ as a way to _namespace_ different configurations and users.<br>
A realm in Keycloak is essentially a __tenant__, which is a space where all the information about a set of users and applications is managed. Each realm is isolated from other realms, allowing for multi-tenancy within a single Keycloak instance.
<br>
Let's imagine we were to have an application that runs two environments: staging and production. We certainly wouldn't want to have staging and production users mixed, so we would have to create two realms (i.e. _staging-realm_ and _production-realm_) to separate the users.<br>
The staging environment would then refer to the first realm, whereas the production one would use the latter.

Keycloak starts, by default, with the __master__ realm already created, which is a management realm, used to create and administrate other realms. You should not use this realm for production usage, because access to this realm can compromise the instance's configuration.
<br>
Let's create a new realm by clicking on the top-left dropdown and onto "Create realm":
![create realm](assets/keycloak_2.png)
Just input its name, we don't have a resource file, and then proceed.
![create realm - name](assets/keycloak_3.png)

<!-- TOC --><a name="clients"></a>
### Clients
Let's talk about how applications can interact with Keycloak, meaning, how can applications perform all the flows and tasks we mentioned before?<br>
Keycloak, and, in general, all OAuth2 servers, introduces the concept of _clients_, that represents something, an application, trying to use OAuth2 functionality to access a protected resources.<br>
There are __confidential clients__ and __public__ clients:

- confidential clients are applications that are able to securely authenticate with the authorization server. In our previous example, the confidential client is the auth microservice. Basically, confidential clients are the middlemen between the user and the authorization server. Confidential clients are represented by a `client_secret` and a `client_id`;
- public clienst are basically applications running in a browser or on a mobile device. They are essentialy the end users of access and refresh tokens.

In other words, public clients rely on confidential clients to obtain credentials and then they use the latter to access private resources.
<br>
Let's see how to create a client and how to use them.
<!-- TOC --><a name="creating-a-confidential-client-in-keycloak"></a>
#### Creating a confidential client in Keycloak
<div style="display: flex; flex-direction: column;">
On the left panel of Keycloak's interface, click on "Clients":

<img src="assets/keycloak_4.png" style="height: 600px; align-self: center">
</div>
In the following screen, click on "Create client".
<br>

You can just enter the __client id__ and then proceed:
![create client - 2](assets/keycloak_5.png)


It's now time to select which flows our client will support. The most common are the __Standard flow__ and the __Service accounts roles__, which are the ones we described before.
![create client - 3](assets/keycloak_6.png)
Clicking on "Client authentication" will make our client _confidential_, and that's what we want, because _non-confidential_ clients are deprecated (still supported for legacy purposes).<br>
The "Login settings" screen is very important, because here we can define the callback URLs for our authorization flows that support them (essentialy, the standard flow). Let's take a look at these options:
![create client - 4](assets/keycloak_7.png)
These settings refer to the authentication microservice's URL for a certain application (the microservice backend in previous picture):

- Root URL refers to the backend's main entry point, basically the domain name without any path attached to it;
- Home URL refers to the application's homepage, if any;
- Valid redirect URIs refer to the URIs we want to use when authenticating a user with our OAuth2 server. Let's bring the diagram for the authorization flow back and see the exact point these URIs get called:
![create client - redirect uris](assets/auth_code_flow_marked.png)
This is how the OAuth2 server and the backend communicate and exchange information about the user that wants to login. Redirect URIs are called, basically, when the OAuth2 server is done authenticating a user and is ready to grant tokens to a certain user. Generally, endpoints specified between these are called with a GET request and a `code` query parameter is passed. This code is the "auth code" we see in the picture and it is exchanged between the backend and the OAuth2 server for the reasons we mentioned before (keeping the client out of the loop for safety reasons). There is the possibility to enter multiple URIs because it might be necessary to support different login flows, but it's generally normal to have just one;
- Valid post logout redirect URIs are endpoints that are called after the user log outs and usually represent the "goodbye" screen you see after logging out of a website;
- Web origins represent the CORS origins allowed to make requests to our OAuth2 server.

In this screen, each field is optional, but at least one valid redirect URI is required if we want to use the standard flow.
For now, we don't have an application to redirect codes to, so we can enter whatever we want.<br>

Even if we don't have an application, we can see how the _standard flow_ works by simulating it:
1. Enter `http://test.whatever` or whatever you want inside the "Valid redirect URIs" field and proceed creating the client. This screen should appear
![client created](assets/keycloak_8.png)
2. Let's click on "Credentials" and copy the "Client Secret" field. This is what we will use to make requests to Keycloak when authenticating someone or an application;
3. Now, on the left-hand side menu, click on "Realm settings" and click <a name="openid-conf-endpoint">"OpenID Endpoint Configuration"</a>;
![openid conf](assets/keycloak_9.png)
A new page should appear displaying JSON information about different endpoints of our Keycloak instance. This is the `openid-configuration` endpoint, that displays various URLs you can request to do various things concering our Keycloak instance. A notable one is the `authorization_endpoint`, which is the URL that gets called when a client wants to login to our system. Another interesting one is the `token_endpoint`, which is the endpoint the auth backend will call in point 6 of the previous diagram;
4. Let's copy the `authorization_endpoint` and start a fictional login flow;
5. To start a login flow, we need to call the `authorization_endpoint` with to __mandatory__ query parameters: `response_type=code` and `client_id=example-client`. The first parameter means that we want to obtain an auth code out of this login flow, and the latter indicates for which confidential client we want this login flow to start for. An example request would be: `http://localhost:8080/realms/test-realm/protocol/openid-connect/auth?response_type=code&client_id=example-spring`;
6. Open an incognito window and paste the URL above. We open an incognito window because we are already logged into Keycloak with our admin account and navigating to the URL above would skip the next step; 
7. A similar screen should appear:
![login screen](assets/keycloak_10.png)
8. We can login with our admin account for the moment, we will see how to create users in a bit;
9. After logging in we should be redirected to the redirect URI we specified before, which, in our case, should be `http://test.whatever`;
10. We get redirected to such address with a couple more parameters added:
![redirect screen](assets/keycloak_11.png)
We can see `session_state`, `iss`, and `code`. If we actually set up an application, this would be the endpoint that, when called, would make the request to obtain the access and refresh tokens (step 6 of the diagram);
11. We can simulate this request with [Postman](https://www.postman.com/downloads/), an HTTP client;
12. To obtain a token we need to make a `POST` request to the `token_endpoint` we talked about in step 3 of this list. Let's do this and add a couple of parameters to the body:
![postman - 1](assets/postman_1.png)

    - we specify the `client_id` we are making the request for, along with its `client_secret`;
    - we specify the `grant_type`, which is the type of credentials we want to get. In this case it's `authorization_code`;
    - let's also add the `code` we received when we got redirected to `http://test.whatever` in step 10 of this list;
    - let's click on "Send" and wait for the response.
13. This is what you should see:
    ![postman - 2](assets/postman_2.png)
14. We got to the end of the standard flow! Nice! We have everything we need to make authenticated requests to our microservices, if we had any.

Note: we will use Postman throughout the entirety of chapters III - V. We will then add a frontend and see how the redirect URIs come into place.<br>
I know this might all be confusing for first-time readers, but don't worry: I was too when I first learned about all this stuff! Don't sweat it, everything will fall into place by the end of this guide!
<hr>

Next chapter: [Chapter II: the system and its components — how everything connects](Chapter%20II.md) 
