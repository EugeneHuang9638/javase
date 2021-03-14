package io.netty.funcdemo.official.factorialptorocol;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Handler for a client-side channel.  This handler maintains stateful
 * information which is specific to a certain channel using member variables.
 * Therefore, an instance of this handler can cover only one channel.  You have
 * to create a new handler instance whenever you create a new channel and insert
 * this handler to avoid a race condition.
 */
public class FactorialClientHandler extends SimpleChannelInboundHandler<BigInteger> {

    private ChannelHandlerContext ctx;
    private int receivedMessages;
    private int next = 1;
    final BlockingQueue<BigInteger> answer = new LinkedBlockingQueue<BigInteger>();

    public BigInteger getFactorial() {
        BigInteger bigInteger = null;
        try {
            // 主线程阻塞在这里，等待channelRead0方法往队列中添加元素，这里才会被解除阻塞
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
            ctx.write(Integer.valueOf(next));
            next++;
        }
        ctx.flush();
    }

}
