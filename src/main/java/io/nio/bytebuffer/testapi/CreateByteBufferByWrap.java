package io.nio.bytebuffer.testapi;

import java.nio.ByteBuffer;

/**
 * 传入一个byte数组来构建缓存区
 *
 *
 */
public class CreateByteBufferByWrap {


    public static void main(String[] args) {
        // 初始值：a, b, c
        byte[] bytes = new byte[]{(byte) 97, (byte) 98, (byte) 99};

        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        // 修改bytes数组中的值 ===> 改成  n
        bytes[2] = (byte) 110;

        System.out.println("修改数组后的值：byteBuffer的值也发生了变化");
        while (byteBuffer.remaining() > 0) {
            System.out.println((char) byteBuffer.get());
        }

    }

}
