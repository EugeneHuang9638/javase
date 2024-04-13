package io.netty.funcdemo.official.chat2;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * ps：这里的frame代表桢的意思。
 *
 * webSockets在传输的过程中，是在帧里面来发送数据，其中每一个都代表了一个消息的一部分。一个完成的消息可以利用多个帧。
 * 举个例子方便理解：
 *   websocket是一个双方在当个长链接进行双向传输数据的协议。websocket为了解决大消息的问题，因此，会对大消息做分桢操作。
 *   每个消息块都有自己的协议，当尾部标识为0时，代表消息还没有结束。当尾部标识为1时，表示当前桢是最后一条消息。
 *
 * 在RFC通用规范中，为websocket一共定义了6种不同的frame；
 * 分别为：
 *   1、连续桢（Continuation Frames）：标记为0x0  连续桢用于携带属于上一个文本或二进制桢的延续数据。
 *       当一个非常大的消息被分成多个桢发送时，除了第一个桢外，其他桢都被标记为连续桢。
 *   2、文本桢（Text Frames）：标记为0x1  文本桢用于传输文本数据。文本数据必须为有效的UTF-8编码
 *   3、二进制桢（Binary Frames）：标记为0x2  二进制桢用于传输二进制数据。
 *   4、关闭帧（Close Frames）：标记为0x8  关闭帧用于通知对方关闭连接。
 *   5、Ping帧（Ping Frames）：标记为0x9  Ping帧用于测试连接是否正常。
 *   6、Pong帧（Pong Frames）：标记为0xA  Pong帧用于响应Ping帧。
 *
 *
 * netty为它们每个都提供了一个POJO实现，而我们的程序只需要使用下面4个帧类型；
 *   closeWebSocketFrame：对应的是关闭桢
 *   PingWebSocketFrame：对应的ping桢
 *   PongWebSocketFrame：对应的pong桢（用于响应ping）
 *   TextWebSocketFrame：对应的是文本桢
 * 在这里，我们只需要显示处理TextWebSocketFrame，其他的会由WebSocketServerProtocolHandler自动处理
 *
 *
 */
public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception { // 1
        // 当客户端往服务端写信息时，广播给所有的channel。内容为websocket text类型的桢
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            if (channel != incoming) {
                channel.writeAndFlush(new TextWebSocketFrame("[" + incoming.remoteAddress() + "]" + msg.text()));
            } else {
                // 自己
                channel.writeAndFlush(new TextWebSocketFrame("[you]" + msg.text()));
            }
        }
    }

    /**
     * 客户端与服务端握手成功后，会回调此方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        // 发送给其他客户端
        channels.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + "] 加入"));
        // 将当前客户端链接的channel添加到channels中
        channels.add(incoming);
        System.out.println("add");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        // 发送给其他客户端
        channels.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + "] 离开"));
        // 将当前客户端链接的channel从channels中移除（因为一个channel被关闭后，会自动的从channelGroup中移除，所以无需指定remove）
        // channels.remove(incoming);
    }

    /**
     * 什么时候被调用？
     * 服务端监听到客户端的活动时被调用。
     * 但：所谓的“活动”是指什么意思？
     * --- 猜测服务端跟客户端有一个心跳检测机制，如果对方是存货的，则回调此方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception { // 5
        Channel incoming = ctx.channel();
        System.out.println("Client: " + incoming.remoteAddress() + "在线");
    }

    /**
     * 什么时候被调用？
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // 6
        Channel incoming = ctx.channel();
        System.out.println("Client: " + incoming.remoteAddress() + "掉线");
    }
}
