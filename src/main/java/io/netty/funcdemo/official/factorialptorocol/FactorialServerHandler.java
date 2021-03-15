package io.netty.funcdemo.official.factorialptorocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.math.BigInteger;

/**
 * 服务端handler
 * 逻辑比较简单：客户端针对每个数据包做一次乘法计算，并响应给客户端
 */
public class FactorialServerHandler extends SimpleChannelInboundHandler<BigInteger> {

    private BigInteger lastMultiplier = new BigInteger("1");

    // 存储阶乘计算结果参数
    private BigInteger factorial = new BigInteger("1");

    @Override
    public void channelRead0(ChannelHandlerContext ctx, BigInteger msg) throws Exception {
        // Calculate the cumulative factorial and send it to the client.
        lastMultiplier = msg;
        // 客户端每发送一个数字过来，就计算一遍，并把计算后的结果响应给客户端
        factorial = factorial.multiply(msg);
        ctx.writeAndFlush(factorial);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.err.printf("Factorial of %,d is: %,d%n", lastMultiplier, factorial);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
