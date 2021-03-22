package io.netty.funcdemo.chat.solution;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Iterator;

/**
 * 自定义handler需要继承netty规范好的某个handlerAdapter（规范）
 */
public class ChatServerHandler extends SimpleChannelInboundHandler<ChatPacket> {

    /**
     * GlobalEventExecutor：顾名思义，是一个全局的事件执行器，单例的。
     * 现在只是把这个对象拿到，并放到channelGroup中
     */
    private static ChannelGroup CHANNEL_GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 每个客户端连接上服务器时，都会回调这个方法
     * 此时我们将客户端的channel保存到服务端，
     * 然后在客户端发送消息时，遍历所有的客户端，达到群发的目的
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String content = "[客户端：" + channel.remoteAddress() + "] 上线了";
        byte[] bytes = content.getBytes();
        int length = bytes.length;
        CHANNEL_GROUP.writeAndFlush(new ChatPacket(length, bytes));

        // 在客户端中维护所有连接到服务器的channel
        CHANNEL_GROUP.add(channel);
        System.out.println("[客户端：" + channel.remoteAddress() + "] 上线了，当前连接客户端数量：" + CHANNEL_GROUP.size());
    }

    /**
     * 读取客户端发送的数据
     * @param ctx 上下文对象，含有通道channel，管道pipeline
     * @param msg 客户端发送的数据
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatPacket msg) throws Exception {
        // 当前发送消息给服务器的客户端channel
        Channel currentChannel = ctx.channel();
        // 将msg转化成ByteBuf，类似于NIO的byteBuffer

        // 向每个客户端发送数据
        String msgData = new String(msg.getData());
        System.out.println("[客户端：" + currentChannel.remoteAddress() + "]发来的消息：" + msgData);
        Iterator<Channel> iterator = CHANNEL_GROUP.iterator();
        while (iterator.hasNext()) {
            Channel channel = iterator.next();
            String content;
            if (currentChannel == channel) {
                content = "[自己]发送的消息：" + msgData;
            } else {
                content = "[客户端：" + currentChannel.remoteAddress() + "]发来的消息：" + msgData;
            }

            /**
             * =============
             *  核心地方：组装成指定的数据包，发送给客户端
             * =============
             */
            byte[] bytes = content.getBytes();
            int length = bytes.length;
            channel.writeAndFlush(new ChatPacket(length, bytes));
        }
    }

    /**
     * 处理异常，一般是关闭通道
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    /**
     * 客户端与服务器断开连接会回调到此方法
     * 此时服务器应该给每个客户端发送离线的消息，并把CHANNEL_GROUP中离线的客户端移除
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("[客户端：" + channel.remoteAddress() + " ]下线了");
        Iterator<Channel> iterator = CHANNEL_GROUP.iterator();
        while (iterator.hasNext()) {
            Channel channelInner = iterator.next();
            if (channelInner == channel) {
                // 移除
                iterator.remove();
            } else {
                String content = "[客户端：" + channel.remoteAddress() + " ]下线了";
                byte[] bytes = content.getBytes();
                int length = bytes.length;
                channel.writeAndFlush(new ChatPacket(length, bytes));
            }
        }
    }
}
