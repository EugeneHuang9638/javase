package io.netty.funcdemo.official.discardserver.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Handles a server-side channel.
 * ==> 服务端的handler
 *
 * 1、DiscardServerHandler继承了ChannelInboundHandlerAdapter。
 * ChannelInboundHandlerAdapter提供了许多的事件处理方法，我们可以重写它们。
 * 而在当前这个案例中，我们直接继承就可以了，不需要自己去重新实现它。
 * 2、我们重写了channelRead方法。在任何时候，接收到客户端发来的消息时，这个方法都会被调用。
 * 3、为了实现Discard协议，我们在channelRead方法中对msg进行了释放。在handle中，它有一个责任：
 *    就是我们接收到消息后，应该要把消息给释放掉。通常，我们会使用try & finally的组合 以及
 *    ReferenceCountUtil.release(msg) api来将资源给释放掉。因此，我们可以改造channelRead方法为：
 *    try {
 *        // not do nothing
 *    } finally {
 *        ReferenceCountUtil.release(msg)
 *    }
 * 4、当有Throwable发生时，exceptionCaught方法就会被调用。
 *    通常情况下，当发生异常时，我们要记录日志，并且将当前关联的channel给关闭
 *    我们可以在这里做很多事情，比如在关闭channel之前给客户端响应一段与业务相关的错误信息
 * 7、ChannelHandlerContext提供了针对不同I/O事件的操作。在这儿：我们调用了write方法去写接收到的消息。
 *    在这儿，我们没有显示的去调用release方法。这是因为在netty中的write方法内部为我们调用了release方法
 * 8、write方法不会将信息写到网络中。flush方法能确保将数据写在网络中。同时，如果为了简洁，我们可以调用
 *    ctx.writeAndFlush(msg)方法来将write和flush操作合二为一
 *
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 只要是客户端发来的消息，都会执行这个方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // ===================== 版本一：实现discard server
        // Discard the received data silently.  --> 默默的丢弃接收到的数据
        ByteBuf in = ((ByteBuf) msg);
        /**
         * Decreases the reference count by 1 and deallocates this object if the reference count reaches at 0
         * javadoc中写到：每次执行release方法时，引用数量减少1，当减少到0时，将会解除分配。
         *
         * 目测是：将客户端发送给服务端的数据给释放掉
         */
//        in.release();

        // ===================== 版本二：证明服务器正常运行

        // 改造后的内容，当有客户端发送信息时，服务端打印一句话，证明服务器是正常运行的。
        // 或者可以使用如下代码变得更简单：System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));
        /*ByteBuf in = (ByteBuf) msg;
        try {
            while (in.isReadable()) { // (5)
                System.out.print((char) in.readByte());
                System.out.flush();
            }
        } finally {
            ReferenceCountUtil.release(msg); // (6)
        }*/
        System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));

        // ===================== 版本三：修改成echo协议，能够回传接收到的信息
        ctx.write(msg);  // (7)
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /**
         * Close the connection when an exception is raised.
         *
         * 当有异常发生时，关闭客户端连接
         */
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }
}
