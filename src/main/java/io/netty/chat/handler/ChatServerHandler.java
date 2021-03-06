package io.netty.chat.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * 自定义handler需要继承netty规范好的某个handlerAdapter（规范）
 */
public class ChatServerHandler extends SimpleChannelInboundHandler {

    /**
     * 每个客户端连接上服务器时，都会回调这个方法
     * 此时我们将客户端的channel保存到服务端，
     * 然后在客户端发送消息时，遍历所有的客户端，达到群发的目的
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    /**
     * 读取客户端发送的数据
     * @param ctx 上下文对象，含有通道channel，管道pipeline
     * @param msg 客户端发送的数据
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("服务端读取线程：" + Thread.currentThread().getName());

        // 将msg转化成ByteBuf，类似于NIO的byteBuffer
        String message = ((ByteBuf) msg).toString(CharsetUtil.UTF_8);

        // 向每个客户端发送数据
        System.out.println("客户端发送的数据：" + message);
    }

    /**
     * 客户端发送的数据读取完成
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端数据读取完成！准备给客户端发送数据。。");
        ByteBuf byteBuf = Unpooled.copiedBuffer("我收到你的消息了".getBytes(CharsetUtil.UTF_8));
        ctx.writeAndFlush(byteBuf);
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
}
