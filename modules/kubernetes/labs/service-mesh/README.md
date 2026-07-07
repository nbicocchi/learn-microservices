# Service Mesh Lab

Before you begin, ensure you have configured an empty k3d cluster as described in the [Istio slides](../../slides/8%20-%20Istio.md).

This project contains the pre-configured files from the [service-routing example](../../../infrastructure/labs/service-routing).

Additionally, you will find a set of Kubernetes YAML manifests to deploy the application to your cluster. These templates are located in the `templates` directory.

You can apply them using `kubectl` with Kustomize:

```shell
kubectl apply -k templates
```