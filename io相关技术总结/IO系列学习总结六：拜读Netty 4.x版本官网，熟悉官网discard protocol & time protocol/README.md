# 前言

* 对于Netty这种通讯框架而言，它最简单的一个实现（**也就是我们常说的Hello World程序**）也得需要客户端和服务端进行通讯，而这实际上就是一种**协议**。在Netty官网中有提到，最简单的协议实现就是：**Discard Protocol**，在这种协议下，服务端和客户端不需要做任何事情。它的最主要功能就是：**接收客户端的请求，但是却丢弃它**。通过**Discard Protocol**的入门程序编写，我们可以了解Netty的开发模式。当然，Netty是一个通讯的框架，我们最后还得通过官网的**Time Protocol**来了解Netty是如何传输信息的、如何自定义编解码器来帮助我们实现自定义协议。
* 注意：下列内容中的注释包含1、2、3.....序号，它与代码后面（1）、（2）、（3）......一一对应

## 一、两张图解释Discard protocol核心代码含义

* 服务端启动入口：**DiscardServer**

  ![discard-protocol.png](discard-protocol.png)

* DiscardServer服务的处理器：**DiscardServerHandler**

  ![DiscardServerHandler.png](DiscardServerHandler.png)

* DiscardServerHandler的版本一共有三个，每个版本都是对discard 协议的一种优化

### 版本一：默默丢弃

* 此版本的代码实现非常简单，直接将读到的数据给释放了。代码如下：

  ```java
  ByteBuf in = ((ByteBuf) msg);
  in.release();
  ```

* 我们如何证明服务器是正常运行的呢？可以使用**telnet**命令（**如下两个版本也使用同样的方式证明**）

### 版本二：证明服务器正常运行

* 在**版本一**中，我们无法证明服务器是否正常运行，因此我们需要将客户端发来的数据打印出来，来证明服务器是正常运行的。在这种情况下，我们对DiscardServerHandler进一步改造，改造代码如下：

  ```java
  ByteBuf in = (ByteBuf) msg;
  try {
      while (in.isReadable()) { // (5)
          System.out.print((char) in.readByte());
          System.out.flush();
      }
  } finally {
      ReferenceCountUtil.release(msg); // (6)
  }
  
  // 或使用更简单的方式
  ByteBuf in = (ByteBuf) msg;
  System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));
  ```

### 版本三：证明服务器和客户端互相通信

* 在版本二中，就算我们在channelRead方法中将客户端发来的话打印出来以证明服务器能正常接收到客户端的数据，但是我们无法证明客户端是否能接收到服务端发来的消息，即无法证明客户端和服务器的通讯是**互相**正常的。在这种情况下，我们对DiscardServerHandler进一步改造，改造代码如下：

  ```java
  // 添加会写数据代码
  ctx.write(msg); 
  ctx.flush();
  ```

* 在此版本中，服务器向客户端回传了数据，这里使用了write方法。需注意：write方法只是将数据写到了缓存区中，此时并未将数据写到网络中去，需要使用flush方法实现此功能。此时的版本三，在Netty官网中，称它为：**[ECHO Protocol 点击查看官网](https://netty.io/wiki/user-guide-for-4.x.html#writing-an-echo-server)**。

## 二、Time protocol协议的实现

* 在官网中，针对Time protocol协议的demo一共做了四次优化，分别是：**基本功能版本、修复碎片化功能版本（包含两种解决方案）、使用pojo传输信息版本**。咱们的就按部就班的跟着官网的demo来，还是能学到很多东西的。其Time protocol的demo主要功能为：**只要有客户端连接到服务器，服务器就把当前的时间响应给客户端。**

### 2.1 基本功能版本

* 此版本也比较简单，服务端代码比较简单，与Discard protocol的服务端代码相比，只有对应的**handle**不一样。其代码如下所示：

  ![TimeServer.png](TimeServer.png)

* 服务端handler

  ![TimeServerHandler.png](TimeServerHandler.png)

* 客户端代码

  ![TimeClient.png](TimeClient.png)

* TimeClientHandler代码

  ![TimeClientHandler.png](TimeClientHandler.png)

  time protocol协议的基本代码如上所示，总共分为：**服务端 & 服务端handler + 客户端 & 客户端handler**。其主要的一些点都在注释中有描述，可以仔细阅读。就这么一个简单的程序可能会存在数据包**碎片化**的问题（**这个问题需要牢牢记住，它应该就是Netty所谓的粘包拆包了**）

### 2.2 netty数据包的碎片化情况

* 

### 2.2 修复碎片化功能版本（解决方案一：特殊的协议使用特殊的方式）

* 先说说为什么会出现**碎片化**的情况，这里引用下官网的解释：

  > In a stream-based transport such as TCP/IP, received data is stored into a socket receive buffer. Unfortunately, the buffer of a stream-based transport is not a queue of packets but a queue of bytes. It means, even if you sent two messages as two independent packets, an operating system will not treat them as two messages but as just a bunch of bytes.

  大致的意思是：**基于流传输的协议，比如TCP/IP，他们都是从缓冲区中接收的。不幸的是，这个基于流协议的缓冲区不是一个数据包的队列，而是一个字节队列。这就意味着，当你发送两条单独的数据包时，操作系统可能不会向对待两个数据包的形式去对待，而是以一串字节的数据来对待。**这会出现什么情况呢？举个例子：假设你发送的数据包是下面这样的，一共发了三个数据包，

  ![data_1.png](data_1.png)

  但操作系统可能会这样处理：

  ![data_2.png](data_2.png)

  

  

