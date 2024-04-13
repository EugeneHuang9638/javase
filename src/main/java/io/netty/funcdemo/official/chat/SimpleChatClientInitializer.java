package io.netty.funcdemo.official.chat;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 聊天室服务端的初始化接口（for：客户端）
 */
public class SimpleChatClientInitializer extends ChannelInitializer<SocketChannel> {


    /**
     *
     * @param ch
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                // 服务端 -> 客户端链路：添加一个解码器，以换行为间隔，防止粘包、粘包
                .addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                // 服务端 -> 客户端链路：
                // 通常与DelimiterBasedFrameDecoder配套使用，DelimiterBasedFrameDecoder只是解决传输上的粘包、粘包问题，
                // 需要解析服务端返回的数据，因此需要解码
                .addLast("decoder", new StringDecoder())

                // 服务端 -> 客户端链路：解析stringDecoder解码后的数据
                .addLast("handler", new SimpleChatClientHandler())

                // 客户端 -> 服务端链路：用于将客户端发送给服务端的数据，编码成char类型
                .addLast("encoder", new StringEncoder())
                ;

    }
}
