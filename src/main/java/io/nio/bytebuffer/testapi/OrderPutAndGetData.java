package io.nio.bytebuffer.testapi;

import java.nio.ByteBuffer;

/**
 * 使用byteBuffer 存储除boolean以外的基本数据类型
 */
public class OrderPutAndGetData {


    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        System.out.println("put值");
        // 默认put进去的就是byte  占用缓存区的1个字节位置
        byteBuffer.put(new Byte("97"));
        // 占用缓存区的2个字节位置
        byteBuffer.putShort((short) 10);
        // 占用缓存区的4个字节的位置
        byteBuffer.putInt(922);
        // 占用缓存区的8个字节的位置
        byteBuffer.putLong(108L);
        // 占用缓存区的4个字节的位置
        byteBuffer.putFloat(1.7F);
        // 占用缓存区的8个字节的位置
        byteBuffer.putDouble(2.2);
        // 占用缓存区的2个字节的位置
        byteBuffer.putChar((char) 97);

        System.out.println("---------------------------------------");
        System.out.println("执行到这里，一共会占用缓存区的29个字节, 因此limit为1024，potision为29，capacity为1024");
        System.out.println("limit => " + byteBuffer.limit());
        System.out.println("capacity => " + byteBuffer.capacity());
        System.out.println("position => " + byteBuffer.position());

        System.out.println("---------------------------------------");
        byteBuffer.flip();
        System.out.println("get值  ==> 比较重要：一定要按顺序读，否则读取出来的数据自己都不认识！");
        System.out.println(byteBuffer.get());
        System.out.println(byteBuffer.getShort());
        System.out.println(byteBuffer.getInt());
        System.out.println(byteBuffer.getLong());
        System.out.println(byteBuffer.getFloat());
        System.out.println(byteBuffer.getDouble());
        System.out.println(byteBuffer.getChar());

    }

}
