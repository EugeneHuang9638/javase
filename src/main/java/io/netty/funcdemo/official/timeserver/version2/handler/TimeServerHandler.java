package io.netty.funcdemo.official.timeserver.version2.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 1、当有连接被建立，并能交互数据时，channelActive方法会被调用。
 *    当channelActive方法被调用时回传一个32位的int类型的数字代表当前时间
 * 2、为了发送数据，我们需要分配一个能容纳数据的缓冲区。我们需要写32位的int数字，
 *    因此只需要4个字节即可。在此例中，我们使用了ctx.alloc()方法获取到了ByteBufAllocator。
 *    进而创建需要容纳数据的缓冲区。
 * 3、在编写NIO程序时，我们要往缓冲区写数据之前，都要执行java.nio.ByteBuffer.flip()方法。
 *    在这里为什么不用了呢？那是因为ByteBuf基于NIO的ByteBuffer进行了二次封装，它内部存在两个
 *    指针，一个是reader指针，另一个是write指针。其中
 *    已经读取的区域：[0, readerIndex)
 *    可读取的区域：[readerIndex, writerIndex)
 *    可写区域：[writerIndex, capacity)
 *    在使用netty读写数据时，可以使用netty提供的byteBuf类，可以避免NIO中ByteBuffer的各种麻烦操作，
 *    减少bug的产生。
 *
 *    另外一个关注点就是，ChannelHandlerContext的write或者writeAndFlush方法最终都会返回一个ChannelFuture。
 *    一个channelFuture代表着一个IO操作还并没有发生，针对于所有返回ChannelFuture 的操作，这些操作都是异步处理的。
 *     eg：ChannelHandlerContext的write、writeAndFlush和close方法。
 *     如果代码是这样写的：
 *     ctx.writeAndFlush(time);
 *     ctx.close();
 *     这也并不意味着writeAndFlush比close先执行完。
 *     但是如果一个channelFuture执行完了之后，它会通知所有监听到它的监听者。
 * 4、在此处，我们为writeAndFlush方法返回的channelFuture添加了一个监听器，当channelFuture执行完毕后，会通知注册到
 *    channelFuture的所有监听器。在这个例子中，当writeAndFlush方法执行完毕后，会通知我们自己写的匿名内部类，
 *    其主要的作用就是优雅的关闭channel。
 * 5、我们可以使用Netty框架本身提供的关闭channel的监听器来简化我们的代码
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception { // (1)
        final ByteBuf time = ctx.alloc().buffer(4); // (2)
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

        final ChannelFuture f = ctx.writeAndFlush(time); // (3)
        /*f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                assert f == future;
                ctx.close();
            }
        });*/ // (4)

        f.addListener(ChannelFutureListener.CLOSE); // （5）
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
