package io.netty.funcdemo.chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.funcdemo.chat.handler.ChatClientHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class NettyClient {

    public static void main(String[] args) {
        EventLoopGroup clientEventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(clientEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 为客户端添加一个处理器，只处理字符串。
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("encoder", new StringEncoder());
                            pipeline.addLast(new ChatClientHandler());
                        }
                    });

            System.out.println("开始启动netty客户端");
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();
            Channel channel = channelFuture.channel();
            Scanner scanner;
            while (true) {
                scanner = new Scanner(System.in);
                // 设置scanner遇到换行符才读取数据，默认是遇到空格和换行都会读取数据的
                scanner.useDelimiter("\n");
                String next = scanner.next();

                channel.writeAndFlush(next);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clientEventLoopGroup.shutdownGracefully();
        }
    }
}
