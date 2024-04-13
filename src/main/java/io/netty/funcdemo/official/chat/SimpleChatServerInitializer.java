package io.netty.funcdemo.official.chat;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 聊天室服务端的初始化接口（for：服务端）
 */
public class SimpleChatServerInitializer extends ChannelInitializer<SocketChannel> {


    /**
     *
     * @param ch
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
                // 添加一个解码器，以换行为间隔，防止粘包、粘包
                .addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                // 通常与DelimiterBasedFrameDecoder配套使用，DelimiterBasedFrameDecoder只是解决传输上的粘包、粘包问题，
                // 但获取到的还是字节数据，需要转化成字符串。因此需要StringDecoder解码器
                .addLast("decoder", new StringDecoder())
                // 处理真正的聊天室逻辑
                .addLast("handler", new SimpleChatServerHandler())
                .addLast("encoder", new StringEncoder())
        ;

        // todo 待校验，是不是每次客户端链接都会打印这个？
        // 目测会：因为客户端每次连接，都会产生一个新的channel并交给worker去处理
        System.out.println(String.format("SimpleChatClient: %s 连接上", ch.remoteAddress()));

    }
}
