package io.netty.funcdemo.official.timeserver.version4.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.funcdemo.official.timeserver.version4.handler.TimeEncoder;
import io.netty.funcdemo.official.timeserver.version4.handler.TimeServerHandler;

public class TimeServer {

    private int port;

    public TimeServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();// (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)// (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() {// (4)
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TimeEncoder(), new TimeServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)// (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);// (6)

            // 绑定并开始接收连接请求
            ChannelFuture f = b.bind(port).sync();// (7)

            // 等待，直到server socket被关闭. 在discard协议中，这种情况不会被发生。但是我们应该
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new TimeServer(port).run();
    }
}
