package io.netty.funcdemo.official.heartbeat;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * 在netty中一共有三种超时类型：
 * ALL_IDLE: 一段时间内没有数据接收或者发送
 * READER_IDLE: 一段时间内没有数据接收
 * WRITER_IDLE: 一段时间内没有数据写入
 *
 * 针对这三种枚举，netty有对应的三种channelHandler，分别为：
 *   IdleStateHandler：超时状态处理，需要指定读、写、all 的超时时间
 *   ReadTimeoutHandler：读超时状态处理
 *   WriteTimeoutHandler：写超时状态处理
 *
 * @author muyang
 * @create 2024/4/14 21:16
 */
public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {

    // 定义了心跳时要发送的内容
    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(
            Unpooled.copiedBuffer("Heartbeat", CharsetUtil.UTF_8)
    ); // 1

    /**
     * 判断是否是idleStateEvent事件，如果是，则处理
     *
     * 当channel有事件发生时，我们可以做自定义处理
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) { // 2
            IdleStateEvent event = (IdleStateEvent) evt;
            String type = "";
            if (event.state() == IdleState.READER_IDLE) {
                // 读空闲
                type = "read idle state";
            } else if (event.state() == IdleState.WRITER_IDLE) {
                type = "write idle";
            } else if (event.state() == IdleState.ALL_IDLE) {
                type = "all idle";
            }

            // 将心跳内容发送给客户端
            ChannelFuture future = ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()); // 3
            future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            System.out.println(ctx.channel().remoteAddress() + "超时类型：" + type);
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
}
