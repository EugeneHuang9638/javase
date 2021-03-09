package io.netty.funcdemo.official.timeserver.version4.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.funcdemo.official.timeserver.version4.handler.TimeClientHandler;
import io.netty.funcdemo.official.timeserver.version4.handler.TimeDecoder;

public class TimeClient {

    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";
        int port = Integer.parseInt("8080");
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(worker)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new TimeDecoder());
                            pipeline.addLast(new TimeClientHandler());
                        }
                    });

            // Start the client
            ChannelFuture f = b.connect(host, port).sync(); // (6)

            // 同步等待，直到服务器被关闭
            f.channel().closeFuture().sync();

        } finally {
            worker.shutdownGracefully();
        }
    }
}
