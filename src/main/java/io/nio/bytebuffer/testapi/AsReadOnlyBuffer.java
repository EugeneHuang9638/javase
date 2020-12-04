package io.nio.bytebuffer.testapi;


import java.nio.ByteBuffer;

/**
 * 测试copy出来一个只读的缓存区
 */
public class AsReadOnlyBuffer {

    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);

        // 获取到的类型为：HeapByteBufferR  => 看源码可知：所有关于写的方法全部是抛异常
        ByteBuffer readOnlyByteBuffer = byteBuffer.asReadOnlyBuffer();
        // 尝试写内容  -->  直接抛异常：Exception in thread "main" java.nio.ReadOnlyBufferException
        readOnlyByteBuffer.put((byte) 97);

    }
}
