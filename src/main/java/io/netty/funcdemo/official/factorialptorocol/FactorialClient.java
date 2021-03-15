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
 * 递归协议客户端，连接 127.0.0.1 的8322端口，并计算数字4的阶乘
 */
public final class FactorialClient {

    static final String HOST = "127.0.0.1";
    static final int PORT = 8322;
    static final int COUNT = 4;

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
                            pipeline.addLast(new BigIntegerEncoder());
                            pipeline.addLast(new FactorialClientHandler());
                        }
                    });

            /**
             * 连接服务器，后面的sync方法保证了connect操作是同步执行的。
             * =================================
             * 注意点：netty中所有返回值为channelFuture的操作都是异步的。
             * =================================
             */
            ChannelFuture f = b.connect(HOST, PORT).sync();

            /**
             * 获取最后面的一个handler，这里就是FactorialClientHandler，
             * 这里也与28行的添加顺序有关，pipeline内部存储handler的结构是一个双向链表，
             * 我们添加进去的handler最终会挨个的接到链表后面。
             */
            FactorialClientHandler handler =
                    (FactorialClientHandler) f.channel().pipeline().last();

            /**
             * main线程执行到FactorialClientHandler的getFactorial方法后，
             * 由于内部是一个阻塞队列，在客户端未将数据填充到队列中时，它将
             * 一直处于阻塞状态
             */
            getResult(handler);
        } finally {
            group.shutdownGracefully();
        }
    }

    private static void getResult(FactorialClientHandler handler) {
        System.err.format("Factorial of %,d is: %,d", COUNT, handler.getFactorial());
    }
}
