# 心跳检测demo

## 一、原理

* 在netty框架中，已经内置了**心跳检测**的功能，它是一个handler，位于pipeline中。其内部实现的主要核心原理是**延迟任务**。在我们编写聊天室demo程序时，我们添加了netty框架内置的String类型的编解码器，编解码器也是一个handler。同理，我们如果要实现心跳检测功能的话，只需要添加内置的**心跳检测**handler即可。

## 二、netty内置心跳检测handler - IdleStateHandler

* 官方对IdelStateHandler的描述：

  > Triggers an IdleStateEvent when a Channel has not performed read, write, or both operation for a while

  大致意思就是：当channel没有发生read、write或者read，write两个操作都没有发生时，将会触发**IdleStateEvent**事件。

  IdleStateHandler的构造器中有三个重要的参数，分别是：

  > 1、readerIdleTime：读事件空闲时间，若读事件空闲时间超过设置的时间，那么将会触发IdleState.READER_IDLE事件
  >
  > 2、writerIdleTime：写事件空闲时间，若写事件空闲时间超过设置的时间，那么将会触发IdleState.WRITER_IDLE事件
  > 3、allIdleTime：读写事件空闲时间，若读和写两个事件的空闲时间超过设置的时间，那么将会触发IdleState.ALL_IDLE事件

* 由上可知，IdleStateHandler的处理器仅仅是出发了某个特殊的事件，根据我们之前编写netty程序的经验，我们必须还需要编写一个handler来处理对应的事件。**注意：netty提供了一个叫`io.netty.channel.ChannelInboundHandlerAdapter#userEventTriggered`的方法来处理读空闲事件、写空闲事件、读写空闲事件以及其他事件（这里还需要确认还有哪些事件会触发）**

### 2.1 IdleStateHandler实现心跳检测的原理

* 在开头就对IdleStateHandler处理器做了总结，其实它内部就是利用了延迟任务的机制来完成心跳检测功能的。因为对于主业务而言，心跳检测机制不应该影响主业务，因此使用延迟任务来实现功能能完美解决问题。

* 因为我们把IdleStateHandler处理器添加到了pipeline中，因此IdleStateHandler内部的一些其他事件的钩子函数也会被执行，在**io.netty.handler.timeout.IdleStateHandler#channelActive**钩子函数中（有客户端连接服务器成功时，会调用此钩子函数）有一个叫**initialize**的方法。在initialize方法内部，主要就是根据传入的readerIdleTime、writerIdleTime、allIdleTime参数来开启对应触发事件的定时任务。eg：我们设置了readerIdleTime为60s，那么在读事件空闲60s后，对应的定时任务会发布**IdleState.READER_IDLE**事件，再去执行对应的**userEventTriggered**方法。但由于userEventTriggered方法可能也会在其他事件产生时被调用，因此，我们在处理时判断下事件的类型，如下所示：

  ```java
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
      if (evt instanceof IdleStateEvent) {
          IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
          switch (idleStateEvent.state()) {
              case READER_IDLE:
                  System.out.println("读事件空闲，可以主动关闭channel通道，释放空闲资源");
                  // 同步关闭资源
                  ctx.channel().close().sync();
                  break;
              case WRITER_IDLE:
                  System.out.println("写事件空闲");
                  break;
              case ALL_IDLE:
                  System.out.println("读写事件空闲");
                  break;
          }
      } else {
          System.out.println("其他事件类型");
      }
  }
  ```

  在上述的demo中，我们针对读空闲时间做了特殊处理：**释放资源（关闭客户端的连接）**。

## 三、心跳检测机制有什么用

* 用处非常大，我们所知的rocket mq、dubbo的dubbo协议、nacos注册中心等开源框架的心跳检测机制就是使用netty的IdleStateHandler处理器实现的。