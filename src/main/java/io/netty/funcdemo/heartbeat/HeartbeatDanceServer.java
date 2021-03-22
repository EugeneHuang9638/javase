package io.netty.funcdemo.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class HeartbeatDanceServer {

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建服务端的启动对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            // 使用链式编程配置参数
            bootstrap.group(bossGroup, workerGroup) // 设置两个线程组
                    .channel(NioServerSocketChannel.class) // 使用NioServerSocketChannel作为服务器的通道实现
                    // 初始化服务器连接队列大小，服务端护理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接
                    // 多个客户端同时来的话，服务端将不能处理的客户端连接请求放在队列中等待处理
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 创建通道初始化对象，设置初始化参数
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 对workerGroup的socketChannel新增心空闲状态处理器  <=> 心跳检测
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(new StringDecoder());
                            /**
                             * 配置一个限制状态的handler，其主要功能就是我们说的心跳检测。
                             * 此handler的构造器中主要核心参数为4个：
                             * 1、long readerIdleTime  ==> 读空闲时间：顾名思义，当读事件空闲的时间超过设置的时间时，会触发对应的读超时事件
                             * 2、long writerIdleTime  ==> 写空闲时间：顾名思义，当写事件空闲的时间超过设置的时间时，会触发对应的写超时事件
                             * 3、long allIdleTime     ==> 读写空闲时间：顾名思义，当读写事件的空闲事件超过设置的事件时，会触发对应的读写超时事件
                             * 4、unit ==> 上述三个数字的时间单位。
                             *
                             * 通常，不会让此handler放在最后面，如果放在最后面就会导致IdleStateHandler变得无意义。因为此
                             * handler触发对应的读事件超时、写事件超时、读写数据超时会触发一些事件，这些事件只有后面的handler
                             * 才能感知到。因为pipeline中的handler是顺序执行的，当前handler触发的事件只有后面的handler能感知到
                             */
                            pipeline.addLast(new IdleStateHandler(3, 0, 0, TimeUnit.SECONDS));
                            pipeline.addLast(new HeartbeatServerHandler());
                        }
                    });
            System.out.println("开始启动服务器");

            ChannelFuture channelFuture = bootstrap.bind(9000).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
