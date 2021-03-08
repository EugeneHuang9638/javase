package io.netty.funcdemo.official.timeserver.handler;

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
 *
 */
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception { // (1)
        final ByteBuf time = ctx.alloc().buffer(4); // (2)
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));

        final ChannelFuture f = ctx.writeAndFlush(time); // (3)
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                assert f == future;
                ctx.close();
            }
        }); // (4)
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
