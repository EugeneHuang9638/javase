package io.netty.funcdemo.official.factorialptorocol;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 客户端的handler，主要处理BigInteger类型的数据
 */
public class FactorialClientHandler extends SimpleChannelInboundHandler<BigInteger> {

    private ChannelHandlerContext ctx;
    private int receivedMessages;
    private int next = 1;
    final BlockingQueue<BigInteger> answer = new LinkedBlockingQueue<BigInteger>();

    public BigInteger getFactorial() {
        BigInteger bigInteger = null;
        try {
            /**
             * 主线程阻塞在这里，等待channelRead0方法往队列中添加元素，这里才会被解除阻塞
             *
             * 调用处：
             * @see FactorialClient#getResult(io.netty.funcdemo.official.factorialptorocol.FactorialClientHandler)
             */
            bigInteger = answer.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            return bigInteger;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 客户端一连接到服务器就向服务器发送数据
        this.ctx = ctx;
        /**
         * eg：求数字4的阶乘
         * 最终会向服务端发送1、2、3、4的bigInteger类型
         */
        sendNumbers();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, final BigInteger msg) {
        receivedMessages ++;
        if (receivedMessages == FactorialClient.COUNT) {
            // Offer the answer after closing the connection.
            ctx.channel().close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    boolean offered = answer.offer(msg);
                    assert offered;
                }
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void sendNumbers() {
        for (int i = 0; i < 4096 && next <= FactorialClient.COUNT; i++) {
            /**
             * 这里会发送 {@link FactorialClient.COUNT}次数据包，但此时并还没发送到
             * 网络中，只有调用flush方法后，才是将数据写入到网络中
             */
            ctx.write(new BigInteger(String.valueOf(next)));
            next++;
        }
        ctx.flush();
    }

}
