package io.netty.funcdemo.chat.solution;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ChatEncoder extends MessageToByteEncoder<ChatPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ChatPacket msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getLength());
        out.writeBytes(msg.getData());
    }
}
