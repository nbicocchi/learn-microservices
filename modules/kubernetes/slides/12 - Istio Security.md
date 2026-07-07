# 12 - Istio security

Istio provides the following security features:
- **Mutual pods authentication** via Mutual TLS: [see reference](https://istio.io/latest/docs/tasks/security/authentication/authn-policy)
- **End-user authentication**: configurable JSON Web Token verification per path [see reference](https://istio.io/latest/docs/tasks/security/authentication/authn-policy/#end-user-authentication)
- **Filter HTTP traffic**: allow only traffic with some characteristics between pods [see reference](https://istio.io/latest/docs/tasks/security/authorization/authz-http/)
- **Authorization**: give different kubernetes accounts different permissions [see reference](https://istio.io/latest/docs/concepts/security/#authorization)

```mermaid
graph LR
    Source["⚠️ External Attacker / <br/> Compromised Pod"] -.->|"Malicious HTTP Request <br/> (e.g., missing JWT, wrong path)"| Pod

    subgraph Pod ["Target Kubernetes Pod"]
        direction LR
        Envoy["🛡️ Envoy Proxy <br/> (Sidecar)"] 
        App["📦 Application Container"]
    end

    %% Routing inside the pod
    Source --> Envoy
    Envoy -- "✅ Allowed" --> App

    %% Security Policies
    AuthN["Authentication Policy"] -.->|"1. Verifies mTLS & JWT"| Envoy
    AuthZ["Authorization Policy"] -.->|"2. Verifies RBAC & HTTP Rules"| Envoy

    %% Rejection
    Envoy -.->|"❌ Denied (401 / 403)"| Dropped["Traffic Dropped"]

    %% Styling
    classDef danger fill:#ffebee,stroke:#c62828,stroke-width:2px,color:#c62828;
    classDef podbox fill:#f8f9fa,stroke:#4285f4,stroke-width:2px,stroke-dasharray: 5 5;
    classDef proxy fill:#fff3e0,stroke:#ea4335,stroke-width:2px,color:#202124;
    classDef safe fill:#e8f5e9,stroke:#34a853,stroke-width:2px,color:#202124;
    classDef drop fill:#b71c1c,stroke:#fff,stroke-width:2px,color:#fff;
    classDef policy fill:#f1f3f4,stroke:#5f6368,stroke-width:1px;

    class Source danger;
    class Pod podbox;
    class Envoy proxy;
    class App safe;
    class Dropped drop;
    class AuthN,AuthZ policy;
```