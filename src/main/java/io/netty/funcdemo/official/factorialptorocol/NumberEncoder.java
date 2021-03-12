package io.netty.funcdemo.official.factorialptorocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.math.BigInteger;

/**
 * Encodes a {@link Number} into the binary representation prepended with
 * a magic number ('F' or 0x46) and a 32-bit length prefix.  For example, 42
 * will be encoded to { 'F', 0, 0, 0, 1, 42 }.
 */
public class NumberEncoder extends MessageToByteEncoder<Number> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Number msg, ByteBuf out) {
        // Convert to a BigInteger first for easier implementation.
        BigInteger v;
        if (msg instanceof BigInteger) {
            v = (BigInteger) msg;
        } else {
            v = new BigInteger(String.valueOf(msg));
        }

        // Convert the number into a byte array.
        byte[] data = v.toByteArray();
        int dataLength = data.length;

        /**
         * 这儿有个非常基础但都不怎么关注的点：
         * writeByte是写一个字节进去，因此占用1个字节， --->  这个字节为：F的ASCII码
         * writeInt是写一个int类型的数据进去，因此占用4个字节  ---> 这个int类型的数据为：实际BigInteger值对应的byte数组长度
         * writeBytes是写一个byte数组进去，实际占用多少字节取决于数组的长度
         *
         * 那如何解码呢？
         * 首先先获取第一个字符读取一个byte类型的值，如果是F对应的ASCII码，则继续后面的处理。
         * 然后再获取4个字节读取一个int类型的数字，来表示后面byte数组的长度，即还需要往后面读取多少位字节
         * 再获取byte数组的长度的数据，得到客户端发送的数据。
         *
         * 整个流程为：
         *   客户端循环发送1,2,3,4
         *     -> 客户端编码时，写了6个字节进去，第1个字节为字母F的ascii码，
         *        剩下的4个字节为实际BigInteger值对应的byte数组长度的类型
         *        剩下要写多少字节，取决于实际BigInteger值对应的byte数组长度。
         *        eg：第一次循环发送1时，会以F00011的格式发送到服务端。
         *        最终发送给服务端的一串数据为：F00011F00012F00013F00014
         *        -> 服务端解码，发现可读区域有24个字节，于是按照同样的规则进行解码。
         *           首先，获取第一个字节，得到F，则表示这是当前数据包的开头，可以进一步处理。
         *                 如果这次数据的开头不是F的话，那就是有异常的，服务器可以直接拒绝处理这个请求
         *           其次，继续往后读取4个字节（因为客户端与服务端定了协议，后面的四个字节为一个int类型），
         *                 得到0001，转成int类型后，它就是1
         *           最后，得到上面的1后，再往后读取一个字节，即：1，最后将1转化成字节数组，最终转化成BigInteger
         *                 因此，前6个字节F00001最终表示的BigInteger的值为：1。
         *           -> 读取完整个数据后，服务端需要计算结果，在这个demo中，服务端每次解析完后都会向客户端发送数据。
         *              因为计算4的阶乘时，服务端要想客户端发送4次数据。
         */
        out.writeByte((byte) 'F'); // magic number
        out.writeInt(dataLength);  // data length
        out.writeBytes(data);      // data
    }
}
