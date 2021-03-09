package io.netty.funcdemo.official.timeserver.version4.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.funcdemo.official.timeserver.version4.pojo.UnixTime;
import io.netty.util.ReferenceCountUtil;

/**
 * 这个处理处理程序有时会引发：IndexOutOfBoundsException的异常，
 * 我们将在下一个章节中讨论它为什么会发生
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            UnixTime m = (UnixTime) msg;
            System.out.println(m);
            ctx.close();
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
