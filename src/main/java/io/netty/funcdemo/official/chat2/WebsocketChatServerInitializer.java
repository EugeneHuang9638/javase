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
                // 如下三个channelHandler是用来处理http协议的操作。因为当前聊天室的功能逻辑是这样的：
                // 访问localhost:8000 此时会访问netty服务器，因为uri不是/ws1结尾，因此认为是http协议，此时会通过http协议将websocketchatclient.html渲染给浏览器，用来渲染
                // 当浏览器渲染websocketchatclient.html页面后，就可以在这个页面中做聊天操作了
                // 其中HttpServerCodec是http协议的编解码器，比较重要，如果你的协议是http，那一定要添加这个channelHandler。它的作用是将字节流转成http请求，以及将响应转换成直接流传输给浏览器
                // HttpObjectAggregator也是http协议比较重要的对象。主要是将多个消息对象（HttpRequest、HttpContent、LastHttpContent）聚合成当个FullHttpRequest或FullHttpResponse对象
                // ChunkedWriteHandler是用来方便的写入大量和数据，而不是将整个数据加载到内存中。就是针对于很大的数据流传输，使用零拷贝技术，避免占用jvm大量内存。常用的场景为：解析文件或数据库的数据流
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
