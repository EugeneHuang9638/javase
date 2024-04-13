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
                // 如下四个channelHandler是用来处理http协议的操作。因为当前聊天室的功能逻辑是这样的：
                // 访问localhost:8000 此时会访问netty服务器，因为uri不是/ws1结尾，因此认为是http协议，此时会通过http协议将websocketchatclient.html渲染给浏览器，用来渲染
                // 当浏览器渲染websocketchatclient.html页面后，就可以在这个页面中做聊天操作了
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
