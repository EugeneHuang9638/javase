package io.netty.funcdemo.official.factorialptorocol;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.ssl.SslContext;

/**
 * Creates a newly configured {@link ChannelPipeline} for a server-side channel.
 *
 * 1、与我们之前写的netty demo差不多，都是为了给socketChannel配置一些channelHandler
 *    在此处配置了多种handler，其中包括：编解码时压缩数据、BigInteger的解码器、number的编码器
 *    以及最后处理阶乘的业务逻辑handler。
 */
public class FactorialServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public FactorialServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) { // （1）
        ChannelPipeline pipeline = ch.pipeline();

        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }

        // Enable stream compression (you can remove these two if unnecessary)
        pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

        // Add the number codec first,
        pipeline.addLast(new BigIntegerDecoder());
        pipeline.addLast(new NumberEncoder());

        // and then business logic.
        // Please note we create a handler for every new channel
        // because it has stateful properties.
        pipeline.addLast(new FactorialServerHandler());
    }
}
