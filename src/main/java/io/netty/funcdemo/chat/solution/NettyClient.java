package io.netty.funcdemo.chat.solution;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

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
                            pipeline.addLast(new ChatDecoder());
                            pipeline.addLast(new ChatEncoder());
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

                byte[] bytes = next.getBytes();
                int length = bytes.length;
                channel.writeAndFlush(new ChatPacket(length, bytes));
            }

            /**
             * 测试拆包粘包：
             * 第一步：将36-45行的代码注释打开，开启第一个客户端
             * 第二步：将下面注释部分的代码打开，开启第二个客户端
             * 第三步：到第一个客户端中去查看，看服务端发送给客户端的数据都是怎样的。（可以重复运行第二步和第三步，来查看不同的现象）
             *
             * 解决方案：以固定的格式告诉我当前这条消息的数据有多长，告诉服务端什么时候可以开始拆解数据了，以及拆解多少数据
             */
//            for (int i = 0; i < 25; i++) {
//                String content = "老哥 你好呀！我是avengerEug" + i;
//                byte[] bytes = content.getBytes();
//                int length = bytes.length;
//                channel.writeAndFlush(new ChatPacket(length, bytes));
//            }
//            System.in.read();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            clientEventLoopGroup.shutdownGracefully();
        }
    }
}
