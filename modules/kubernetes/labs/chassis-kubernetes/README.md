# Chassis Kubernetes lab
This lab contains a Kubernetes YAML configuration for running the system described at [chassis-java/labs/chassis-demo](../../../chassis/chassis-java/labs/chassis-demo) 
on a Kubernetes cluster.  
Before starting, you have to upload to Docker Hub the containers defined in `chassis-demo`. If you do not know how to do
it, or you do not want to do it, those containers are already available on my Docker Hub account ([mattev02 Docker Hub](https://hub.docker.com/search?q=mattev02)).

Please follow the [Kubernetes Introductive lesson](../../slides/01%20-%20Fundamentals%20and%20K3D.md) to configure and 
run locally a Kubernetes cluster with `k3d`.

To apply the YAML configuration, just run the following command:
```shell
kubectl apply -f chassis-kubernetes.yaml
```

Please refer to the [lesson content](../../slides/02%20-%20First%20cluster.md) for details.
