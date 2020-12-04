package io.nio.bytebuffer.testapi;

import java.nio.ByteBuffer;

/**
 * 创建directByteBuffer ==> 堆外内存，位于操作系统中。省去了
 * 从jvm 拷贝数据到 操作系统的步骤
 *
 * DirectByteBuffer（由ByteBuffer.allocate() api创建）与HeapByteBuffer（由ByteBuffer.allocate() api创建）最大的区别就是：
 * directByteBuffer存储数据的byte数组是存在操作系统中的，
 * 而heapByteBuffer存储数据的byte数组存在jvm的堆中。
 * 当我们要进行网络传输数据时，最终肯定要调用操作系统的函数，
 *
 * 而操作系统在进行网络传输数据之前，必须在操作系统中开辟一块
 * 内存来对数据进行传输。
 * 因此，当我们使用heapByteBuffer来传输数据时，操作系统还需要将
 * jvm中的内存拷贝到操作系统内存中去才进行传输。
 * 而我们使用directByteBuffer时，直接省去了将内存拷贝到操作系统的步骤
 *
 *
 * directByteBuffer （由Buffer.allocateDitrct方法创建）是基于堆外内存的（位于操作系统中），
 * jvm中只保存了一个地址，指向堆外内存。而heapByteBuffer则是基于堆内内存的，byte数组保存在jvm中，
 * 当要进行网络交互时，需要把堆内内存 拷贝 一份到操作系统中，然后操作系统再基于这个内存进行网络传输
 *
 */
public class CreateDirectByteBuffer {


    public static void main(String[] args) {
        //
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(10);


    }
}
