package io.netty.resourcecodelearning;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.funcdemo.base.handler.NettyServerHandler;

/**
 * 以一个简单的官网demo来阅读netty服务端的源码
 *
 * 记录一个问题：
 *
 *public NioServerSocketChannel(){
        *this(newSocket(DEFAULT_SELECTOR_PROVIDER));
        *}
 *
 * 这种无参构造方法调用有参构造方法，最终会有几个对象产生呢？
 *
 */
public class OfficialServerDemo {


    public static void main(String[] args) {
        // 主从线程模型 =》 一主多从
        EventLoopGroup bossGroup = null;
        EventLoopGroup workerGroup = null;
        try {


            /**
             * 第一行代码：创建boosGroup的EventLoopGroup
             *
             * 指定了线程数为1，Executor传入null（内部使用默认的executor：new ThreadPerTaskExecutor(newDefaultThreadFactory());），
             * 使用的select策略工厂为默认策略工厂 -> DefaultSelectStrategyFactory.INSTANCE
             *
             * 内部同时维护了一个“事件执行器”，它是一个EventExecutor类型的数组（其实就是NioEventLoopGroup），
             * 针对于每一个线程（根据传入的线程数）有一个事件执行器来执行这个线程触发的事件。
             * 在事件执行器内部维护了一个队列，用来存储事件执行器需要执行的事件，
             * 还维护了一系列NIO相关的组件，eg: Selector等等
             */
            bossGroup = new NioEventLoopGroup(1);
            /**
             * 同上，唯一不同的就是使用的是默认的线程数，默认时：线程数量为cpu核心数 * 2
             */
            workerGroup = new NioEventLoopGroup();

            /**
             * 服务器启动对象，空构造方法，内部的一些属性都是一些初始值。
             */
            ServerBootstrap bootstrap = new ServerBootstrap();
            /**
             * 使用链式编程配置参数
             * 目的就是：将服务器一些关键的信息通过java代码的方式填充进去。
             * group方法：维护内部叫group和childrenGroup的两个变量名，group：boosGroup、childrenGroup：workerGroup
             * channel方法：维护内部一个叫channelFactory的属性，其value为ReflectiveChannelFactory类型的对象（内部维护了传入NioServerSocketChannel的无参构造器对象）
             * options方法：添加内部一个叫options的concurrentHashMap，存储的是一些参数信息
             * childHandler方法：添加内部一个叫childHandler的属性，在这里，传入的是一个匿名内部类。
             */
            bootstrap.group(bossGroup, workerGroup) // 设置两个线程组
                    .channel(NioServerSocketChannel.class) // 使用NioServerSocketChannel作为服务器的通道实现
                    // 初始化服务器连接队列大小，服务端护理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接
                    // 多个客户端同时来的话，服务端将不能处理的客户端连接请求放在队列中等待处理
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 创建通道初始化对象，设置初始化参数
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 对workerGroup的socketChannel设置处理器
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("开始启动服务器");


            /**
             * 上述的一系列步骤类似于spring的register方法，
             * 其主要功能就是填充一些基础信息，为后续的一系列工作做了铺垫
             */


            /**
             * bind方法，类似于spring的refresh方法，真正的开始服务和绑定端口
             * 1、{@link AbstractBootstrap#initAndRegister()} 方法初始化了channel（利用了channelFactory内部维护的无参构造器对象创建出来了一个NioServerSocketChannel实例），
             *   因此，我们需要去看下NioServerSocketChannel的内部结构。
             *   其内部包含了nio服务器的一系列信息，在NioServerSocketChannel对象的构建过程中做了如下几件事：
             *   1.1、使用java nio创建open了一个serverSocketChannel，获取到了一个channel
             *   1.2、内部维护了SelectionKey.OP_ACCEPT事件的属性，目前还没与channel绑定起来（后续绑定端口后再拿这个属性注册到selector中去）
             *   1.3、父类配置了channel为非阻塞模式
             *
             * TODO 源码阅读到 {@link NioServerSocketChannel#NioServerSocketChannel(java.nio.channels.ServerSocketChannel)}
             * 的第一行代码。后面的代码还没来得及分析。
             */
            ChannelFuture channelFuture = bootstrap.bind(9000).sync();

            // 给channelFuture注册监听器
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (channelFuture.isSuccess()) {
                        System.out.println("监听端口9000成功");
                    } else {
                        System.out.println("监听端口9000失败");
                    }
                }
            });

            // 对通道关闭进行监听，closeFuture是异步操作，监听通道关闭
            // 通过sync方法同步等待通道关闭处理完毕，这里会阻塞等待通道关闭完成。
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
