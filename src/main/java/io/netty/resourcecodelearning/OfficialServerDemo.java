package io.netty.resourcecodelearning;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.funcdemo.base.handler.NettyServerHandler;

/**
 * 以一个简单的官网demo来阅读netty服务端的源码
 */
public class OfficialServerDemo {


    public static void main(String[] args) {
        // 主从线程模型 =》 一主多从
        EventLoopGroup bossGroup = null;
        EventLoopGroup workerGroup = null;
        try {


            /**
             * 第一行代码：创建bossGroup的EventLoopGroup
             *
             * 指定了线程数为1，Executor传入null（内部使用默认的executor：new ThreadPerTaskExecutor(newDefaultThreadFactory());），
             * 使用的select策略工厂为默认策略工厂 -> DefaultSelectStrategyFactory.INSTANCE
             *
             * 内部同时维护了一个“事件执行器”，它是一个EventExecutor类型的数组（其实就是NioEventLoopGroup），
             * 针对于每一个线程（根据传入的线程数）有一个事件执行器来执行这个线程触发的事件。
             * 在事件执行器内部维护了一个队列，用来存储事件执行器需要执行的事件，
             * 还维护了一系列NIO相关的组件，eg: Selector等等
             */
            bossGroup = new NioEventLoopGroup(1);
            /**
             * 同上，唯一不同的就是使用的是默认的线程数，默认时：线程数量为cpu核心数 * 2
             */
            workerGroup = new NioEventLoopGroup();

            /**
             * 服务器启动对象，空构造方法，内部的一些属性都是一些初始值。
             */
            ServerBootstrap bootstrap = new ServerBootstrap();
            /**
             * 使用链式编程配置参数
             * 目的就是：将服务器一些关键的信息通过java代码的方式填充进去。
             * group方法：维护内部叫group和childrenGroup的两个变量名，group：boosGroup、childrenGroup：workerGroup
             * channel方法：维护内部一个叫channelFactory的属性，其value为ReflectiveChannelFactory类型的对象（内部维护了传入NioServerSocketChannel的无参构造器对象）
             * options方法：添加内部一个叫options的concurrentHashMap，存储的是一些参数信息
             * childHandler方法：添加内部一个叫childHandler的属性，在这里，传入的是一个匿名内部类。
             */
            bootstrap.group(bossGroup, workerGroup) // 设置两个线程组
                    .channel(NioServerSocketChannel.class) // 使用NioServerSocketChannel作为服务器的通道实现
                    // 初始化服务器连接队列大小，服务端护理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接
                    // 多个客户端同时来的话，服务端将不能处理的客户端连接请求放在队列中等待处理
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 创建通道初始化对象，设置初始化参数
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 对workerGroup的socketChannel设置处理器
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("开始启动服务器");


            /**
             * 上述的一系列步骤类似于spring的register方法，
             * 其主要功能就是填充一些基础信息，为后续的一系列工作做了铺垫
             */


            /**
             * bind方法，其地位类似于spring的refresh方法，真正的开始服务和绑定端口
             * 1、{@link AbstractBootstrap#initAndRegister()} 方法初始化了channel（利用了channelFactory内部维护的无参构造器对象创建出来了一个NioServerSocketChannel实例），
             *   因此，我们需要去看下NioServerSocketChannel的构造方法{@link NioServerSocketChannel#NioServerSocketChannel()}做了哪些事情。
             *   无参构造方法主要是构建了一个ServerSocketChannel对象，然后传递给带参构造方法{@link NioServerSocketChannel#NioServerSocketChannel(java.nio.channels.ServerSocketChannel)}构造方法，
             *   此构造方法一共有两段代码：
             *   1.1、super(null, channel, SelectionKey.OP_ACCEPT);
             *      调用父类的构造方法，并且在父类又调用父类的父类的构造方法。这块逻辑总共做了如下几件事：
             *      1.1.1、为当前channel初始化属于自己的Unsafe（Netty自己开发的）类、pipeline(这里的pipeline目前只有head和tail)。{@link AbstractChannel#AbstractChannel(io.netty.channel.Channel)}
             *
             *      1.1.2、为当前channel填充serverSocketChannel、维护感兴趣的事件（此处为OP_ACCEPT事件，注意：此处还没有和serverSocketChannel进行绑定）、配置serverSocketChannel为非阻塞。{@link AbstractNioChannel#AbstractNioChannel(io.netty.channel.Channel, java.nio.channels.SelectableChannel, int)}
             *   1.2、config = new NioServerSocketChannelConfig(this, javaChannel().socket());
             *      其内部主要作用为：维护了NioServerSocketChannel的配置, 包含刚刚创建的serverSocketChannel的socket对象、channel对象
             *      也就是说config内部包含了channel，channel属于config的一个子集
             *
             * 2、此外，{@link AbstractBootstrap#initAndRegister()} 方法还执行了{@link AbstractBootstrap#init(io.netty.channel.Channel)}方法，
             *    该方法的作用为：
             *    将serverBootstrap中配置的
             *    childGroup（{@link ServerBootstrap#group(io.netty.channel.EventLoopGroup, io.netty.channel.EventLoopGroup) 方法中指定的workGroup}）、
             *    childHandler（{@link ServerBootstrap#childHandler 方法中添加的ChannelInitializer}）、
             *    等配置信息绑定到内部构建的ChannelInitializer（本质上也是一个channelHandler）中去。
             *    最终将这个ChannelInitializer添加到serverSocketChannel的pipeline中去，等待触发。
             *
             *    而这个内部构建的ChannelInitializer（我们称它为 ciA），只重写了initChannel方法，其大致逻辑为：
             *             @Override
             *             public void initChannel(final Channel ch) {
             *                 final ChannelPipeline pipeline = ch.pipeline();
             *                 ChannelHandler handler = config.handler();
             *                 if (handler != null) {
             *                     pipeline.addLast(handler);
             *                 }
             *
             *                 ch.eventLoop().execute(new Runnable() {
             *                     @Override
             *                     public void run() {
             *                         // 我们称它为 ciAInner
             *                         pipeline.addLast(new ServerBootstrapAcceptor(
             *                                 ch, currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs));
             *                     }
             *                 });
             *             }
             *    在这段代码中，ch为serverSocketChannel，
             *    config为当前serverSocketChannel中的config，
             *    也就是说，当执行到ciA的initChannel方法后，
             *    会往serverSocketChannel的线程池中提交一个任务，而这个任务的执行逻辑就是
             *    往serverSocketChannel的pipeline中添加ciAInner，
             *    而这个ciAInner中维护了我们当初在serverBootstrap中配置的childGroup、childHandler的信息。
             *    也就是说，当执行到ciA的initChannel方法后，内部就会执行ciAInner，在ciAInner内部，可能就会执行到我们在
             *    serverBootstrap中配置的childGroup、childHandler了。
             *    那这个方法什么时候被执行呢？ 后面在做分析，目前现在只需要知道，serverSocketChannel
             *    的pipeline有三个channelHandler了，分别为head、tail和ciA
             *
             * 3、随后，在{@link AbstractBootstrap#initAndRegister()} 方法还执行了下面这段代码：
             *    ChannelFuture regFuture = config().group().register(channel);
             *    这里的前半段config().group()拿到的是serverBootstrap中配置的bossGroup对应的NioEventLoopGroup
             *    因此，最终调用的是NioEventLoopGroup的register方法，其中，参数channel为NioServerSocketChannel。
             *    大致的调用链路为：
             *    io.netty.channel.SingleThreadEventLoop#register(io.netty.channel.Channel)
             *      -> io.netty.channel.SingleThreadEventLoop#register(io.netty.channel.ChannelPromise)
             *        -> io.netty.channel.AbstractChannel.AbstractUnsafe#register(io.netty.channel.EventLoop, io.netty.channel.ChannelPromise)
             *          当调用到AbstractUnsafe的register方法后，内部会启动一个线程调用register0方法：io.netty.channel.AbstractChannel.AbstractUnsafe#register0(io.netty.channel.ChannelPromise)
             *          在这个方法中，主要就是做注册的事情了。在初始化channel的时候，我们获取了ServerSocketChannel，也配置成了非阻塞，也有了感兴趣的事件（OP_ACCEPT）和selector，
             *          但是却始终没有将他们注册到selector中去。这一步，就是要注册到selector中去，同时要回调pipeline中一系列的channelHandler。
             *          3.1、在io.netty.channel.nio.AbstractNioChannel#doRegister()方法中，有一个死循环，解除循环的条件就是serverSocketChannel注册到selector了。
             *          因此，此方法的主要目的就是将serverSocketChannel注册到selector中去
             *          3.2、注册完了之后会执行下面这段代码：
             *             pipeline.invokeHandlerAddedIfNeeded();
             *          这里的主要目的就是：netty想要触发register操作之后的回调，但是呢，由于之前没有将对应的钩子函数给绑定好，
             *          因此，此操作就是绑定一些钩子函数，而这里所说的钩子函数就是pipeline中的channelHandler。当然，方法名中
             *          有IfNeeded的字眼，这就表示，如果需要的话，则添加，这相当于是一种扩展吧，防止一些额外逻辑。
             *          但是在此处，如果channelHandler被添加了，则会回调ChannelInitializer类型的channelHandler中的handlerAdded方法，
             *          而在此方法内部会回调它的initChannel方法。 还记得上面总结的ciA channelHandler吗，就是在此处被调用的。
             *          所以，执行完此方法后，serverSocketChannel内部的pipeline中就多了一个叫ServerBootstrapAcceptor的channelHandler了。
             *          3.3、执行完invokeHandlerAddedIfNeeded方法后，就是要在register操作成功后要触发的方法了。
             *          而netty使用的是责任链设计模式来完成这个功能的，其实也可以用发布订阅的模式（类似于spring回调后置处理器）
             *
             * 至此，服务端的netty启动程序的主逻辑就是这样啦，有几个比较关键的点：
             * 1、每一个channel内部都会包含一个pipeline
             * 2、每一个pipeline初始化的时候都只有一个head和tail
             * 3、serverSocketChannel注册到selector后会触发一个add的钩子函数，此钩子函数执行完毕后，会往pipeline中添加
             *    一个叫ServerBootstrapAcceptor的channelHandler，此handler内维护了workgroup的相关信息，后续将由它来
             *    处理客户端的非OP_ACCEPT的所有请求
             * 4、netty每执行完一系列操作后，都会触发一系列的钩子函数。相当于在netty交互的生命周期中都有触发一些钩子函数。
             *    这种设计思想还是值得学习的，扩展性非常高。
             *
             *
             *
             */
            ChannelFuture channelFuture = bootstrap.bind(9000).sync();

            // 给channelFuture注册监听器
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (channelFuture.isSuccess()) {
                        System.out.println("监听端口9000成功");
                    } else {
                        System.out.println("监听端口9000失败");
                    }
                }
            });

            // 对通道关闭进行监听，closeFuture是异步操作，监听通道关闭
            // 通过sync方法同步等待通道关闭处理完毕，这里会阻塞等待通道关闭完成。
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
