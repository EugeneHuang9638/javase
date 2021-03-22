package io.netty.funcdemo.heartbeat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端：" + ctx.channel().remoteAddress() + "连接了");
        super.channelActive(ctx);
    }

    /**
     * 用户事件触发器，这里的用户事件可以有很多种，在心跳检测中的demo中，
     * 触发的事件类型为空闲事件类型 {@link IdleStateEvent}
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    System.out.println("读事件空闲，可以主动关闭channel通道，释放空闲资源");
                    // 同步关闭资源
                    ctx.channel().close().sync();
                    break;
                case WRITER_IDLE:
                    System.out.println("写事件空闲");
                    break;
                case ALL_IDLE:
                    System.out.println("读写事件空闲");
                    break;
            }
        } else {
            System.out.println("其他事件类型");
        }
    }
}
