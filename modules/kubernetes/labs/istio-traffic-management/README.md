# Istio traffic management lab

This lab experience contains a YAML configuration file to perform on to `service-management` traffic management: it adds 
a new version of `bff-service` and an Istio Gateway routing that allows user which specify a particular header to access 
the new service. Moreover, this service simulates faults (Error 404). 

To try yourself, first run the cluster specified in [`service-mesh` lab](../service-mesh/README.md), then run
```shell
kubectl apply -f templates
```

Then in 2 separated terminals run the following commands:
```shell
# Terminal 1 - Standard traffic
watch -n 0.5 curl -o /dev/null -s  http://GATEWAY_IP/bff/87fdf189-d2ef-45f1-9d58-31ee62a5f5d4

# Terminal 2 - Beta users
watch -n 0.8 curl -o /dev/null -s -H \"x-beta-version: True\" http://172.20.0.200/bff/87fdf189-d2ef-45f1-9d58-31ee62a5f5d4
```

If you open Istio dashboard and open the Traffic Graph you will see that some requests from beta users return with code 404.
```shell
istioctl dashboard grafana
```

Please refer to the [lesson content](../../slides/11%20-%20Istio%20traffic%20management.md) for details.