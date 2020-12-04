package io.nio.bytebuffer.testapi;

import java.nio.ByteBuffer;

/**
 * 测试缓存区的slice方法（拷贝缓存区）
 *
 * 拷贝出来的缓存区和原缓存区共享byte数组的数据
 *
 */
public class ByteBufferSlice {


    public static void main(String[] args) {
        // 创建长度为1的缓存区
        ByteBuffer byteBuffer = ByteBuffer.allocate(7);
        // 往缓存区放一个小写的a字母
        byteBuffer.put((byte) 97);
        byteBuffer.flip();
        System.out.println("缓存区的数据为：");
        System.out.println((char) byteBuffer.get());
        // 还原position的值
        byteBuffer.clear();

        /**
         * 使用slice方法创建出来新的缓存区，此缓存区与原缓存区一模一样
         */
        ByteBuffer byteBufferCopy = byteBuffer.slice();
        // 当我们修改复制出来的缓存区的数据时，原缓存区的数据也会被修改
        byteBufferCopy.put((byte) 98);
        System.out.println("修改拷贝的缓存区的数据后，原缓存区的数据为：");
        System.out.println((char) byteBuffer.get());

    }
}
