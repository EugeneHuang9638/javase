## Netty

### 一、认识Netty

* Netty是一个基于NIO封装的一个网络框架，它的功能非常简单：就是能快速并且很简单的开发出一套高性能的网络应用程序。（官网：https://netty.io）
* 官方文档(**4.1.x版本**)：[https://netty.io/wiki/user-guide-for-4.x.html#wiki-h2-3](https://netty.io/wiki/user-guide-for-4.x.html#wiki-h2-3)

### 二、为什么使用Netty

* JDK有NIO解决了BIO吞吐量低、阻塞的问题，那为什么不使用NIO，而使用Netty呢？这是因为使用NIO非常复杂，我们需要熟练掌握：**Selector、ServerSocketChannel、SocketChannel、ByteBuffer**。特别是ByteBuffer，需要详细了解它的**mark、position、limit、capacity**四个变量的值，稍不留神就容易写出bug。而且，开发网络应用程序会出现很多异常情况：**断连重连、网络闪断、心跳处理、半包读写、网络阻塞、异常流**。在使用NIO开发网络应用程序时，这些异常情况的处理都会将**工作量和难度**加大。而Netty对NIO的api做了良好的封装，良好的解决了上述问题。且**`Netty拥有高性能、高吞吐量、延迟低、减少资源消耗、最小化不必要的内存复制`**等优点。

#### 三、Netty的使用场景：

* 互联网行业：在分布式系统中，各个节点之间需要远程服务调用，高性能的RPC框架必不可少，Netty作为异步高性能的通信框架，往往作为基础通信组件被这些RPC框架使用。典型的就是阿里的dubbo框架使用Dubbo协议进行节点间通信，Dubbo协议默认使用Netty作为基础通信组件。RocketMQ使用的也是Netty作为基础通信组件。
* 其他行业：所有使用java语言开发需要通信的应用， 都会优先选择Netty框架，它本身就提供了TCP/UDP和HTTP协议栈。

### 四、官网拜读

* 以官方文档为例，基于Netty编写一个网络应用程序

* 服务端代码：

  ```java
  
  ```

  