package io.netty.funcdemo.official.chat2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * webSockets在传输的过程中，是在帧里面来发送数据，其中每一个都代表了一个小希的一部分。
 * 一个完成的消息可以利用多个帧。
 * 在RFC通用规范中，为websocket一共定义了六中不同的frame；
 * netty为它们每个都提供了一个POJO实现，而我们的程序只需要使用下面4个帧类型；
 *   closeWebSocketFrame
 *   PIngWebSocketFrame
 *   PongWebSocketFrame
 *   TextWebSocketFrame
 * 在这里，我们只需要显示处理TextWebSocketFrame，其他的会由WebSocketServerProtocolHandler自动处理
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {

    }
}
