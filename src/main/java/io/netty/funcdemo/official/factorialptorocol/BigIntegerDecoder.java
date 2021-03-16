package io.netty.funcdemo.official.factorialptorocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.math.BigInteger;
import java.util.List;

/**
 * bigInteger的解码器：
 * eg：报文 “7000142” 会被解析成new BigInteger("42")
 *
 * 解析逻辑取决于：bigInteger的编解码逻辑，具体可查看编解码逻辑
 */
public class BigIntegerDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // Wait until the length prefix is available.
        /**
         * 防止数据碎片化的拆包流程，eg：
         * 拆包后：  拆包处
         *           |
         * 70 0 0 0  |  1 1 70 0 0 0 1 2 70 0 0 0 1 3 70 0 0 0 1 4
         *           |
         * 客户端本来发送字节数组为：70 0 0 0 1 1 70 0 0 0 1 2 70 0 0 0 1 3 70 0 0 0 1 4
         * 这样的话，70 0 0 0 1 1这个整体就被拆开了
         * 因此这里保证了解码的数据是完整的
         */
        if (in.readableBytes() < 6) {
            return;
        }

        // 校验接收的数据包是否以F开头
        int magicNumber = in.readUnsignedByte();
        if (magicNumber != 'F') {
            in.resetReaderIndex();
            throw new CorruptedFrameException("Invalid magic number: " + magicNumber);
        }

        // 以F开头，则认为是符合规则的数据，则开始处理接收到的数据
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            // 如果缓冲区中可读的字节少于剩余要读取的长度，则直接返回，等待下一次数据包的到来
            in.resetReaderIndex();
            return;
        }

        /**
         * 执行到这里则表示数据包的结构是符合要求的。
         * 接下来需要：
         * 1、读取并转换成真正需要计算的bigInteger，写入到out中
         * 2、标记已经读取的index
         */
        byte[] decoded = new byte[dataLength];
        in.readBytes(decoded);

        in.markReaderIndex();

        out.add(new BigInteger(decoded));
    }
}