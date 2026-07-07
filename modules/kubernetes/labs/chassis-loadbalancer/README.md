# Chassis LoadBalancer lab

In this lab experience, you will add a `LoadBalancer` to the system defined in [`chassis-kubernetes` lab](../chassis-kubernetes).  

To add the `LoadBalancer` run
```shell
kubectl apply -f chassis-loadbalancer.yaml
```

We would expect that an External IP is given to the LoadBalancer. After running `kubectl get services` you will notice that
External IP is stuck to `pending`.

> ❓ **Why?**  
> No external IP can be assigned to it because `k3d` lacks the controllers that provide them.

> ✅ **Solutions**
> 1. **Switch to Ingress**, if you need to use the LoadBalancer internally you can use Cluster IP; if you want to make a
     >    service accessible externally, consider using **Ingress** which is much more feature rich than vanilla LoadBalancer
     >    implementation.
> 2. **Install a LoadBalancer provider**, if you need to use LoadBalancer anyways (e.g., required from an external tool),
     >    follow the guide in the Addendum below.

Please refer to the [lesson content](../../slides/05%20-%20Services.md) for details.

## Addendum: Install MetalLB
**MetalLB** is the most used LoadBalancer provider in custom installations.  
`k3d` conflicts with it by default because it uses `traefik` in its control plane. To successfully install MetalLB you
need to start a fresh cluster with `traefik` disabled.
```shell
k3d cluster create metallb-cluster --api-port 6550 --agents 2 --k3s-arg "--disable=servicelb@server:*"
```

It is suggested to install MetalLB via **Helm**, a "packet manager" for your cluster. To install Helm follow the installation
guide on their [website](https://helm.sh/docs/intro/install/).

After the installation run the following commands to install MetalLB.
```shell
helm repo add metallb https://metallb.github.io/metallb
helm repo update
helm install metallb metallb/metallb -n metallb-system --wait --create-namespace
```

Finally, configure MetalLB:
1. get the IPs related with the cluster docker network
```shell
docker network inspect k3d-metallb-cluster | grep Subnet
```

2. Create a file named `metallb-config.yaml` and edit the IP range accordingly to what you saw in the previous step
```yaml
apiVersion: metallb.io/v1beta1
kind: IPAddressPool
metadata:
  name: first-pool
  namespace: metallb-system
spec:
  addresses:
  - 172.21.0.200-172.21.0.250 # Update this range to match your k3d subnet!
---
apiVersion: metallb.io/v1beta1
kind: L2Advertisement
metadata:
  name: homework
  namespace: metallb-system
```

3. Apply the YAML configuration
```shell
kubectl apply -f metallb-config.yaml
```

Now the cluster is ready to create LoadBalancer with a working external IP.
