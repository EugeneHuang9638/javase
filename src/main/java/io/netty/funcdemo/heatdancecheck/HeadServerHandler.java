package io.netty.funcdemo.heatdancecheck;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HeadServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端：" + ctx.channel().remoteAddress() + "连接了");
        super.channelActive(ctx);
    }
}
