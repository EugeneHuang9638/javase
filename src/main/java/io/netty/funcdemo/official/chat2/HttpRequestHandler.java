package io.netty.funcdemo.official.chat2;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * 扩展SimpleChannelInboundHandler用于处理FullHttpRequest信息。
 *
 * 当url是以 ws 结束时，升级成socket协议来处理请求
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> { // 1

    // 默认为ws
    private final String wsUri;
    private static final File INDEX;

    static {
        URL location = HttpRequestHandler.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            String path = location.toURI() + "chat2/WebsocketChatClient.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Unable to locate WebsocketChatClient.html", e);
        }
    }

    public HttpRequestHandler(String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (wsUri.equalsIgnoreCase(request.getUri())) {
            // 如果是ws的uri请求，则交给下一个channelHandler去处理，当前只处理非ws的case
            // 交给下个read类别（即inbound类别的hander）的channelHandler处理
            ctx.fireChannelRead(request.retain()); // 2
            return;
        }

        if (HttpHeaders.is100ContinueExpected(request)) {
            // 什么是http1.1的 100continue请求？
            // 当curl命令发起的post请求中，body的数据超过1024字节时。
            // curl会首先发送一个Except:100-continue(放在请求头中)的请求，询问server是否愿意接受数据
            // 当server返回100-continue应答后，curl才会把发起真正的请求给Server
            // todo：可以用curl命令做一个测试~
            send100Continue(ctx); // 3
        }

        // 读取默认的WebsocketChatClient.htm文件
        RandomAccessFile file = new RandomAccessFile(INDEX, "r"); // 4
        HttpResponse response = new DefaultHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");

        // 判断客户端的请求头中是否有keepalive标识
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        if (keepAlive) { // 5
            // 如果有，则往响应头中添加文本内容的长度 & 告诉客户端，支持对当前连接使用keepalive功能
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
        // 把response写到客户端去，但还没有执行flush操作，因此，还没有真正的写到客户端中
        ctx.write(response); // 6

        // todo：不懂SslHandler 与  DefaultFileRegion和ChunkedNioFile的区别

        if (ctx.pipeline().get(SslHandler.class) == null) { // 7
            // 如果不是加密 也 不压缩，则直接使用nio文件的零拷贝来传输文件
            ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
        } else {
            // 如果要加密，则用nio的ChunkedNioFile来实现文件的传输
            ctx.write(new ChunkedNioFile(file.getChannel()));
        }

        // 将EMPTY_LAST_CONTENT写到客户端去。todo：如果不写，会有什么问题？
        // 猜测：这是传输文件（大数据流）的特定写法。如果要传输文件，就一定需要这么写。  ==》 经过确认：猜测正确
        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT); // 8

        // 如果客户端没有指定keepalive，则把数据刷到客户端中之后执行关闭channel操作
        if (!keepAlive) {
            future.addListener(ChannelFutureListener.CLOSE); // 9
        }

        file.close();


    }

    private void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Client:"+incoming.remoteAddress()+"异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
