# Istio Observability Lab

This lab experience contains a YAML configuration file to add to `service-management` system a custom metrics: it will 
now keep track of the number of requests are made with `x-beta-version`.

To try yourself, first run the cluster specified in [`service-mesh` lab](../service-mesh/README.md), then run
```shell
kubectl apply -f telemetry.yaml
```

Then open Grafana dashboard (by running `istioctl dashboard grafana`) and add the following PromQL query to the dashboard:
```
sum(rate(istio_requests_total{reporter="destination"}[5m])) by (beta_users)
```

Please refer to the [lesson content](../../slides/10%20-%20Istio%20observability.md) for details.