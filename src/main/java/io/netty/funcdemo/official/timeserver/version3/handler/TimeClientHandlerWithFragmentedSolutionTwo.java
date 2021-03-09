package io.netty.funcdemo.official.timeserver.version3.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * 1、ByteToMessageDecoder 也实现了ChannelInboundHandler，它专门用于处理数据碎片化的问题
 * 2、每当有数据被接收时，ByteToMessageDecoder的decode方法会由内部维护的一个累积缓冲区来调用
 * 3、当累积的缓冲区的数量没有达到自定义的长度时，将不往out中添加任何东西。直到下一次来调用时，才读取
 *    4个字节的数据放入out中
 * 4、如果decode方法中往out中添加了数据，则表示timeDecoder解码数据成功，ByteToMessageDecoder将废弃
 *    积累缓冲区中已经读取过的数据。
 *
 * 5、当然，还有一种更简单化的写法，就是使用ReplayingDecoder。
 * ReplayingDecoder是ByteToMessageDecoder的抽象实现类，如果我们使用它的话，我们不需要关心碎片化的情况，
 * 因为它内部也有一个累积缓冲区，只不过如果数据没有塞满的话，会抛出异常
 */
public class TimeClientHandlerWithFragmentedSolutionTwo {

    public static class TimeDecoder extends ByteToMessageDecoder { // (1)
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception { // (2)
            if (in.readableBytes() < 4) {
                return; // (3)
            }

            out.add(in.readBytes(4)); // (4)
        }
    }

    public static class TimeDecoderReplayingDecoder extends ReplayingDecoder<Void> {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            out.add(in.readBytes(4));
        }
    }
}
