package io.netty.funcdemo.chat.solution;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ChatDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        /**
         * 先读取第一个数字，第一个数字为剩下要读取数据的长度
         * 因为第一个数字是int类型，所以判断长度是否大于4
         */
        if (in.readableBytes() >= 4) {
            int readableNum = in.readInt();
            // 校验剩余可读的字节是否满足长度
            if (in.readableBytes() < readableNum) {
                in.resetReaderIndex();
                return;
            }

            byte[] bytes = new byte[readableNum];
            // 读取指定长度的数据
            in.readBytes(bytes);
            out.add(new ChatDataPacket(readableNum, bytes));
        }
    }
}
