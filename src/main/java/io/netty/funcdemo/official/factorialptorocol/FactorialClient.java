package io.netty.funcdemo.official.factorialptorocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Sends a sequence of integers to a {@link FactorialServer} to calculate
 * the factorial of the specified integer.
 */
public final class FactorialClient {

    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8322"));

    // 需要被计算阶乘的数字
    static final int COUNT = Integer.parseInt(System.getProperty("count", "4"));

    public static void main(String[] args) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new BigIntegerDecoder());
                            pipeline.addLast(new NumberEncoder());
                            pipeline.addLast(new FactorialClientHandler());
                        }
                    });

            // Make a new connection.
            ChannelFuture f = b.connect(HOST, PORT).sync();

            // Get the handler instance to retrieve the answer.
            FactorialClientHandler handler =
                    (FactorialClientHandler) f.channel().pipeline().last();

            // Print out the answer.
            System.err.format("Factorial of %,d is: %,d", COUNT, handler.getFactorial());
        } finally {
            group.shutdownGracefully();
        }
    }
}
