# K8s

# 概念总结

| 名词                                                         | 解释                                                         | 个人理解                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| minikube                                                     | k8s官网的一个工具类，可以创建一个集群                        | 一个官方提供的mini版集群                                     |
| **kubectl**                                                  | k8s的命令                                                    | 所有k8s的名称都有它控制                                      |
| **`kubectl create deployment kubernetes-bootcamp --image=gcr.io/google-samples/kubernetes-bootcamp:v1`** | 创建一个名称叫：kubernetes-bootcamp的应用。并且镜像地址为：gcr.io/google-samples/kubernetes-bootcamp:v1 |                                                              |
| kubectl proxy                                                | 创建一个能访问pod的代理。                                    | 默认情况下， k8s内每个pod是有自己的独立网络的，一个集群内可以互相访问。只要是跨了集群，则无法直接访问。通过创建一个代理，可以直接访问内部的pod |
| kubectl get nodes --help                                     | get nodes 是k8s下的一个子命令。-- help表示对子命令的一些帮助提示 | kubectrl为一级命令。get nodes为子命令                        |
| cluster                                                      | 集群。由一群node解决组成                                     |                                                              |
| node                                                         |                                                              | 对应的就是集群中的一台工作机器，是集群的一部分               |
| deployment                                                   |                                                              | 对应一个应用                                                 |
| pod                                                          |                                                              | deployment的实例。简称应用实例；表示一组一个或一组多个应用容器。把pod当成一台电脑，可以部署多个进程 |
| service                                                      | 为一组pod提供流量路由。使用标签选择算符(Selectors)来选择一组Pod | 类似于pod的nginx，做负载均衡。通过集群的ip + service对外暴露端口 可以访问到应用下的pod |
| kubernetes对象                                               | 是持久化的实体。                                             | 创建k8s对象，都需要使用k8s的api。不管是用的命令行工具，还是客户端库，最终都是直接调用k8s的api。k8s的对象包括：deployment、pod、service等等 |
| spec                                                         | 对象规约。几乎每个k8s对象都包含spec字段。                    | 在创建对象时，需要指定对象的内容，比如要几个副本运行。       |
| status                                                       | 状态。几乎每个k8s对象都包含status字段。描述了k8s对象的当前状态 | 在创建对象时，需要指定对象的内容，比如期望有几个副本运行。当某些实例失败了，k8s会通过一系列手段将副本维持到期望状态 |
|                                                              |                                                              |                                                              |

# k8s对象分析

```yml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
spec:
  selector:
    matchLabels:
      app: nginx
  replicas: 2 # 告知 Deployment 运行 2 个与该模板匹配的 Pod
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.14.2
        ports:
        - containerPort: 80
```

如上yml文件信息，是k8s的一个清单(Manifest)文件，描述了一个Deployment的对象。当我们使用 `kubectl apply -f xxx.yml`命令时，则会解析xxx.yml文件并创建对应的k8s对象。

以当前文件信息来看：

| 字段名     | 含义                                                         | 备注 |
| ---------- | ------------------------------------------------------------ | ---- |
| apiVersion | 创建该对象所使用的k8s api的版本                              |      |
| kind       | 想要创建k8s对象的类别                                        |      |
| metadata   | 帮助唯一表示对象的一些数据。包含name字符串、UID和可选的namespace |      |
| spec       | 所期望的该对象的状态。每种对象所需要的格式不一样，具体参考[链接](https://kubernetes.io/zh-cn/docs/reference/kubernetes-api/) |      |
|            |                                                              |      |

