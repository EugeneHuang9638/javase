package io.netty.funcdemo.official.timeserver.version2.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.funcdemo.official.timeserver.version2.handler.TimeClientHandlerWithFragmentedSolutionOne;

/**
 * 1、Bootstrap类似于serverBootstrap，它是专门用于处理客户端的
 * 2、与服务端不同，在客户端中只需要添加一个worker的group即可，boss group只用于服务端
 * 3、替换NioServerSocketChannel。NioSocketChannel只用于客户端的channel
 * 4、客户端没有使用childOption，因为客户端处理的channel为socketChannel，它并没有父channel
 * 5、调用connect方法连接服务器
 */
public class TimeClient {

    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";
        int port = Integer.parseInt("8080");
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(worker) // (2)
                    .channel(NioSocketChannel.class) // (3)
                    .option(ChannelOption.SO_KEEPALIVE, true) // (4)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TimeClientHandlerWithFragmentedSolutionOne());
                        }
                    });

            // Start the client
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            // 同步等待，直到服务器被关闭
            f.channel().closeFuture().sync();

        } finally {
            worker.shutdownGracefully();
        }
    }
}
