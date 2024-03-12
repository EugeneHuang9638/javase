# K8s

# 概念总结

| 名词                                                         | 解释                                                         | 个人理解                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| minikube                                                     | k8s官网的一个工具类，可以创建一个集群                        | 一个官方提供的mini版集群                                     |
| **kubectl**                                                  | k8s的命令                                                    | 所有k8s的名称都有它控制                                      |
| **`kubectl create deployment kubernetes-bootcamp --image=gcr.io/google-samples/kubernetes-bootcamp:v1`** | 创建一个名称叫：kubernetes-bootcamp的应用。并且镜像地址为：gcr.io/google-samples/kubernetes-bootcamp:v1 |                                                              |
| kubectl proxy                                                | 创建一个能访问pod的代理。                                    | 默认情况下， k8s内每个pod是有自己的独立网络的，一个集群内可以互相访问。只要是跨了集群，则无法直接访问。通过创建一个代理，可以直接访问内部的pod |
| kubectl get nodes --help                                     | get nodes 是k8s下的一个子命令。-- help表示对子命令的一些帮助提示 | kubectrl为一级命令。get nodes为子命令                        |
| node                                                         |                                                              | 对应的就是集群中的一台工作机器，是集群的一部分               |
| deployment                                                   |                                                              | 对应一个应用                                                 |
| pod                                                          |                                                              | deployment的实例。简称应用实例；表示一组一个或一组多个应用容器。把pod当成一台电脑，可以部署多个进程 |
| service                                                      | 为一组pod提供流量路由。                                      | 类似于pod的nginx，做负载均衡。通过集群的ip + service对外暴露端口 可以访问到应用下的pod |
|                                                              |                                                              |                                                              |
|                                                              |                                                              |                                                              |

