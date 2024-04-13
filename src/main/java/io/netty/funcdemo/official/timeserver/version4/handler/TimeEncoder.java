package io.netty.funcdemo.official.timeserver.version4.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.funcdemo.official.timeserver.version4.pojo.UnixTime;
import io.netty.handler.codec.MessageToByteEncoder;


/**
 * 1、这一行有许多重要的东西
 *    首先，我们为write方法传入了一个channelPromise，netty在实际将数据写入网络时，可以通过channelPromise来判断
 *
 *    其次，我们只调用了write方法，并没有调用flush方法， 这是因为有一个单独的程序来进行flush操作。
 *    这个单独的程序就是我们的TimeServerHandler。因为我们的TimeServerHandler在客户端连接上来时就会
 *    往客户端写数据，而写数据的这个过程需要编码，此时就会执行到TimeEncoder中去。专业的类做专业的事，
 *    TimeEncoder只需要负责编码即可
 *
 * 这里TimeEncoder继承了MessageToByteEncoder，根据父类的名字可知：
 * MessageToByteEncoder就是一个将消息转换成byte字节的编码器（message可以是我们的任意格式）
 *
 *
 */
public class TimeEncoder extends MessageToByteEncoder<UnixTime> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, UnixTime unixTime, ByteBuf out) throws Exception {
        out.writeInt((int) unixTime.value());
    }
}
