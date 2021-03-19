package io.netty.funcdemo.chat.solution;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatClientHandler extends SimpleChannelInboundHandler<ChatPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatPacket msg) throws Exception {
        System.out.println(new String(msg.getData()));
    }
}
