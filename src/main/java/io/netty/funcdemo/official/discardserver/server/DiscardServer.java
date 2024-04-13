package io.netty.funcdemo.official.discardserver.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.funcdemo.official.discardserver.handler.DiscardServerHandler;

/**
 * 1、NioEventLoopGroup是一个多线程的事件循环处理器。Netty提供了各种EventLoopGroup，每种EventLoopGroup实现了不同的传输协议。
 *   在这个应用程序中，我们实现了一个服务端的应用程序，使用到了两个EventLoopGroup。第一个，经常会被叫做为boss，它主要用来接收
 *   到来的连接。第二个，通常被叫做 worker，它主要是处理接收连接的流量。一旦boss接收到了连接，就会向worker中注册一个accepted连接，
 *   我们可以通过NioEventLoopGroup来设置线程的数量来配置每个channel中使用的线程数量。
 *   ====>  这就是reactor的主从模型，boss主要处理连接，剩下的烂摊子交由worker去做
 * 2、ServerBootstrap是一个助手类。你可以直接使用channel来设置服务器。然而，请注意：这是一个冗长的过程，一般不建议这么做。
 *    ===> 一般直接使用这个助手类来启动即可
 * 3、我们使用NioServerSocketChannel来实例化一个channel去接收到来的连接
 *    ===> 其主要目的就是用来配置当前的serverBootstrap主要处理哪种channel
 * 4、这里的handler通常是被用于一个新channel的计算。而内部传入的ChannelInitializer则是用来帮助计算新的channel的。比如我们在
 *    pipeline中配置了一个新的discardServerHandler。随着应用程序的复杂，我们可能最终会将这种handler提取成一个抽象类。
 *    ===> 这里是我们比较关注的点，如果我们要开发自定义的协议，一般就是在这里面添加东西
 * 5、我们可以设置一些参数，这些参数的设置取决了当前channel的实现，在当前这个例子中，我们写的是一个TCP/IP的服务，所以我们会关注
 *    tcpNoDelay和keepalive（即没有延迟的tcp和保持连接）。具体可以配置哪些参数，我们可以关注官网的
 *    ChannelOption（https://netty.io/4.1/api/io/netty/channel/ChannelOption.html） 和 ChannelConfig（https://netty.io/4.1/api/io/netty/channel/ChannelConfig.html）相关的文档
 * 6、你可能会很好奇，出现了option和childOption两种option。
 *    option服务于NioServerSocketChannel
 *    childOption服务于NioSocketChannel
 * 7、同步绑定端口，并开始接收连接请求
 */
public class DiscardServer {

    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 窗机爱你服务端的启动对象，设置参数
            ServerBootstrap b = new ServerBootstrap();// (2)
            b.group(bossGroup, workerGroup)
                    // 设置服务端通道实现类型
                    .channel(NioServerSocketChannel.class)// (3)
                    // 设置
                    .childHandler(new ChannelInitializer<SocketChannel>() {// (4)
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {   // (4)
                            ch.pipeline().addLast(new DiscardServerHandler());
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

        new DiscardServer(port).run();
    }
}
