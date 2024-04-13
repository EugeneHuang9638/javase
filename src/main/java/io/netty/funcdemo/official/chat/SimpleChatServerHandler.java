package io.netty.funcdemo.official.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * SimpleChannelInboundHandler这个类继承了SimpleChannelInboundHandler，是一个ChannelInboundHandler。
 * 用来处理客户端往服务端的通信流程
 */
public class SimpleChatServerHandler extends SimpleChannelInboundHandler<String> { // 1

    // 保存了一个set集合，用来存储所有链接到服务端的channel
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * todo：什么时候会被调用？
     * 每当服务端收到新的客户端链接时。
     * 会为客户端的连接生成一个channel，并存入到channelGroup中，同时通知给其他客户端
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception { // 2
        // 获取当前channel
        Channel incoming = ctx.channel();

        // 广播一条消息到多个channel中
        channels.writeAndFlush("[SERVER] - " + incoming.remoteAddress() + " 加入\n");

        // 然后把当前channel加进去。（这个为什么不放在前面？ 聊天业务，如果你加入聊天室，那在当前聊天室的其他用户需要感知，所以只需要发送给其他用户即可~）
        channels.add(ctx.channel());
    }

    /**
     *  todo：什么时候会被调用？
     *  每当服务端收到客户端断开操作时，此方法会被回调
     *  此时会拿到当前的channel，并把它并从channelGroup中移除，同时通知给其他客户端
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception { // 3
        // 离开了当前聊天室的用户
        Channel outing = ctx.channel();

        // 如果channel被关闭连接后，将会自动从channelGroup移除，因此不需要执行下段代码
        // channels.remove(outing);

        channels.writeAndFlush("[SERVER] - " + outing.remoteAddress() + " 离开 \n");


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
        System.out.println("SimpleChatClient: " + incoming.remoteAddress() + "在线");
    }

    /**
     * 什么时候被调用？
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception { // 6
        Channel incoming = ctx.channel();
        System.out.println("SimpleChatClient: " + incoming.remoteAddress() + "掉线");
    }


    /**
     * 当客户端往服务端写数据时，会触发此方法的回调。
     * 此方法内部就是把客户端的信息转发给其他客户端。
     *
     * 如果用的是netty 5.0的版本，则channelRead0方法会变成messageReceived
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception { // 4
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            if (channel != incoming) {
                // 发送给别人的信息
                channel.writeAndFlush("[" + incoming.remoteAddress() + "], say: " + msg + System.lineSeparator());
            } else {
                // 自己发送的信息
                channel.writeAndFlush("[you] say: "+ msg  + System.lineSeparator());
            }
        }

    }

    /**
     * 只要channel的处理发生了异常，都会触发此回调
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception { // 7
        Channel currentChannel = ctx.channel();
        System.out.println("SimpleChatClient: " + currentChannel.remoteAddress() + "异常");
        cause.printStackTrace();
        ctx.close();
    }
}
