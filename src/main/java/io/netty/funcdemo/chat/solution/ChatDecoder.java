package io.netty.funcdemo.chat.solution;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 注意点：要根据实际的拆解数据情况来标记已经读取的index
 */
public class ChatDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        /**
         * 先读取第一个数字，第一个数字为剩下要读取数据的长度
         * 因为第一个数字是int类型，所以判断长度是否大于4
         */
        if (in.readableBytes() >= 4) {
            int readableNum = in.readInt();
            // 校验剩余可读的字节是否满足长度。
            if (in.readableBytes() < readableNum) {
                /**
                 * 这里将index重置到数据包的起始位置，这种情况一定是拆包了，我们需要等待数据包的下一次到来
                 */
                in.resetReaderIndex();
                return;
            }

            byte[] bytes = new byte[readableNum];
            // 读取指定长度的数据
            in.readBytes(bytes);
            /**
             * 核心点：一定要标注已经读取的index
             * 否则当出现拆包时，执行到了上面的22行：重置reader的index。
             * 假设不标记已经读取的index的话，执行resetReaderIndex时就会将index至为0.
             * 就相当于所有读取到的数据又会被重新读取一次。
             */
            in.markReaderIndex();
            out.add(new ChatPacket(readableNum, bytes));
        }
    }
}
