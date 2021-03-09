package io.netty.funcdemo.official.timeserver.version2.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.util.Date;

/**
 * 防止客户端处理数据时出现碎片化，
 * 解决方案1：创建一个积累的缓冲区  ---> 符合time protocol协议。知道服务端发来的是一个固定长度的数据
 *
 * 1、利用到了channelHandler的两个生命周期：handlerAdded和handlerRemoved
 *    只要它内部的逻辑不会长时间阻塞，我们可以任意使用它。
 *
 * 2、所有接收到的数据都要往buf里面放
 * 3、在读数据时，我们要校验buf里面是否已经满了（在timeServer中，4个字节就会满），满了之后再处理真实的业务逻辑。
 *    因为我们知道time protocol中返回的数据就是4个字节 = 32位 int类型数字。只有接收到4个字节的数据，才算收到完成
 *    的服务器发来的数据。否则的话，则认为数据没有完全接收到，此时应该等buf的数据区满了再处理。
 *    假设出现了碎片化的情况，那么肯定会有一个数据的读取过程中无法达到累积缓存的长度，假设此时发送的数据少了一位，
 *    那么此时则不会处理任何业务逻辑。当下一波数据发来时，再来完成上一次因数据发生了碎片而导致的异常情况。
 */
public class TimeClientHandlerWithFragmentedSolutionOne extends ChannelInboundHandlerAdapter {

    private ByteBuf buf;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        buf = ctx.alloc().buffer(4);// (1)
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        buf.release();  // (1)
        buf = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            ByteBuf m = (ByteBuf) msg;
            buf.writeBytes(m); // (2)

            if (buf.readableBytes() >= 4) { // (3)
                long currentTimeMillis = (buf.readUnsignedInt() - 2208988800L) * 1000L;
                System.out.println(new Date(currentTimeMillis));
                ctx.close();
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
