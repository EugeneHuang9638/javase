package io.netty.funcdemo.official.chat2;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author muyang
 * @create 2024/4/13 21:23
 */
public class WebsocketChatServerInitializer extends ChannelInitializer<SocketChannel> { // 1


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception { // 2
        socketChannel.pipeline()
                .addLast(new HttpServerCodec())
                .addLast(new HttpObjectAggregator(64 * 1024))
                .addLast(new ChunkedWriteHandler())
                .addLast(new HttpRequestHandler("/ws1"))
                // 这个很重要，必须添加。内部封装了websocket的close、ping、pong桢的处理
                .addLast(new WebSocketServerProtocolHandler("/ws1"))
                .addLast(new TextWebSocketFrameHandler())
        ;


    }
}
