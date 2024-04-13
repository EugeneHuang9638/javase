package io.netty.funcdemo.official.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.Delimiters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 聊天室服务
 */
public class SimpleClientServer {


    public static void main(String[] args) throws InterruptedException, IOException {
        NioEventLoopGroup worker = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new SimpleChatClientInitializer());

            ChannelFuture future = bootstrap.connect("localhost", 8000).sync();
            Channel clientChannel = future.channel();
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String content = in.readLine();
                clientChannel.writeAndFlush(content + System.lineSeparator());
            }
        } finally {
            worker.shutdownGracefully();
        }


    }

}
